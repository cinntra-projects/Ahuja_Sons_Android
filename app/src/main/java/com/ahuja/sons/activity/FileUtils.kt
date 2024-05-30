package com.ahuja.sons.activity

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import com.ahuja.sons.custom.FileUtil.generateFileName
import com.ahuja.sons.custom.FileUtil.getDocumentCacheDir
import java.io.*
import java.util.*

object FileUtils {

    const val DOCUMENTS_DIR = "documents"
    const val AUTHORITY = "YOUR_AUTHORITY.provider"
    const val HIDDEN_PREFIX = "."

    private const val TAG = "FileUtils"
    private const val DEBUG = false

    val sComparator = Comparator<File> { f1, f2 ->
        f1.name.toLowerCase(Locale.ROOT).compareTo(f2.name.toLowerCase(Locale.ROOT))
    }

    val sFileFilter = FileFilter { file ->
        val fileName = file.name
        file.isFile && !fileName.startsWith(HIDDEN_PREFIX)
    }

    val sDirFilter = FileFilter { file ->
        val fileName = file.name
        file.isDirectory && !fileName.startsWith(HIDDEN_PREFIX)
    }

    /**
     * Gets the extension of a file name, like ".png" or ".jpg".
     *
     * @param uri
     * @return Extension including the dot("."); "" if there is no extension;
     * null if uri was null.
     */
    fun getExtension(uri: String?): String? {
        if (uri == null) {
            return null
        }

        val dot = uri.lastIndexOf(".")
        return if (dot >= 0) {
            uri.substring(dot)
        } else {
            // No extension.
            ""
        }
    }

    /**
     * @return Whether the URI is a local one.
     */
    fun isLocal(url: String?): Boolean {
        return url != null && !url.startsWith("http://") && !url.startsWith("https://")
    }

    /**
     * @return True if Uri is a MediaStore Uri.
     */
    fun isMediaUri(uri: Uri): Boolean {
        return "media".equals(uri.authority, ignoreCase = true)
    }

    /**
     * Convert File into Uri.
     *
     * @param file
     * @return uri
     */
    fun getUri(file: File?): Uri? {
        return file?.let { Uri.fromFile(it) }
    }

    /**
     * Returns the path only (without file name).
     *
     * @param file
     * @return
     */
    fun getPathWithoutFilename(file: File?): File? {
        if (file != null) {
            if (file.isDirectory) {
                // no file to be split off. Return everything
                return file
            } else {
                val filename = file.name
                val filepath = file.absolutePath

                // Construct path without file name.
                var pathwithoutname = filepath.substring(0, filepath.length - filename.length)
                if (pathwithoutname.endsWith("/")) {
                    pathwithoutname = pathwithoutname.substring(0, pathwithoutname.length - 1)
                }
                return File(pathwithoutname)
            }
        }
        return null
    }

    fun getPath(context: Context, uri: Uri): String? {
        val absolutePath = getLocalPath(context, uri)
        return absolutePath ?: uri.toString()
    }

    private fun getLocalPath(context: Context, uri: Uri): String? {
        if (FileUtils.DEBUG) Log.d(
            FileUtils.TAG + " File -",
            "Authority: " + uri.authority +
                    ", Fragment: " + uri.fragment +
                    ", Port: " + uri.port +
                    ", Query: " + uri.query +
                    ", Scheme: " + uri.scheme +
                    ", Host: " + uri.host +
                    ", Segments: " + uri.pathSegments.toString()
        )
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // LocalStorageProvider
            if (FileUtils.isLocalStorageDocument(uri)) {
                // The path is the id
                return DocumentsContract.getDocumentId(uri)
            } else if (FileUtils.isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                } else if ("home".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory()
                        .toString() + "/documents/" + split[1]
                }
            } else if (FileUtils.isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                if (id != null && id.startsWith("raw:")) {
                    return id.substring(4)
                }
                val contentUriPrefixesToTry = arrayOf(
                    "content://downloads/public_downloads",
                    "content://downloads/my_downloads"
                )
                for (contentUriPrefix: String in contentUriPrefixesToTry) {
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse(contentUriPrefix),
                        java.lang.Long.valueOf(id)
                    )
                    try {
                        val path: String = getDataColumn(context, contentUri!!, null, null)!!
                        if (path != null) {
                            return path
                        }
                    } catch (e: java.lang.Exception) {
                    }
                }

                // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
                val fileName: String = getFileName(context, uri)!!
                val cacheDir: File = getDocumentCacheDir(context)
                val file: File = generateFileName(fileName, cacheDir)!!
                var destinationPath: String? = null
                if (file != null) {
                    destinationPath = file.absolutePath
                    saveFileFromUri(
                        context,
                        uri,
                        destinationPath
                    )
                }
                return destinationPath
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if (("image" == type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if (("video" == type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if (("audio" == type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(context, contentUri!!, selection, selectionArgs)
            } else if (isGoogleDriveUri(uri)) {
                return getGoogleDriveFilePath(uri, context)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {

            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.lastPathSegment
            } else if (isGoogleDriveUri(uri)) {
                return getGoogleDriveFilePath(uri, context)
            }
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    /**
     * Convert Uri into File, if possible.
     *
     * @return file A local file that the Uri was pointing to, or null if the
     * Uri is unsupported or pointed to a remote resource.
     * @author paulburke
     * @see .getPath
     */
    fun getFile(context: Context, uri: Uri?): File? {
        if (uri != null) {
            val path = getPath(context, uri)
            if (path != null && FileUtils.isLocal(path)) {
                return File(path)
            }
        }
        return null
    }
    /**
     * @return The MIME type for the given file.
     */
    fun getMimeType(file: File?): String {
        val extension = getExtension(file?.name)

        return if (extension!!.isNotEmpty())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1)).toString()
        else
            "application/octet-stream"
    }

    /**
     * @return The MIME type for the given Uri.
     */
      fun getMimeType(context: Context, uri: Uri): String {
        val file = File(getPath(context, uri))
        return getMimeType(file)
    }

    /**
     * @return The MIME type for the given String Uri.
     */
    fun getMimeType(context: Context, url: String?): String {
        var type = context.contentResolver.getType(Uri.parse(url))
        if (type == null) {
            type = "application/octet-stream"
        }
        return type
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is local.
     */
    fun isLocalStorageDocument(uri: Uri): Boolean {
        return AUTHORITY == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    fun isGoogleDriveUri(uri: Uri): Boolean {
        return "com.google.android.apps.docs.storage.legacy" == uri.authority ||
                "com.google.android.apps.docs.storage" == uri.authority
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(
        context: Context, uri: Uri, selection: String?,
        selectionArgs: Array<String>?
    ): String? {

        var cursor: Cursor? = null
        val column = MediaStore.Files.FileColumns.DATA
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                if (DEBUG) DatabaseUtils.dumpCursor(cursor)

                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } catch (e: Exception) {
            // Timber.e(e);
        } finally {
            cursor?.close()
        }
        return null
    }

    fun saveFileFromUri(context: Context, uri: Uri, destinationPath: String) {
        var `is`: InputStream? = null
        var bos: BufferedOutputStream? = null
        try {
            `is` = context.contentResolver.openInputStream(uri)
            bos = BufferedOutputStream(FileOutputStream(destinationPath, false))
            val buf = ByteArray(1024)
            `is`?.read(buf)
            while (`is`?.read(buf) != -1) {
                bos.write(buf)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`?.close()
                bos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun getFileName(context: Context, uri: Uri): String? {
        val mimeType = context.contentResolver.getType(uri)
        var filename: String? = null

        if (mimeType == null && context != null) {
            val path = getPath(context, uri)
            if (path == null) {
                filename = getName(uri.toString())
            } else {
                val file = File(path)
                filename = file.name
            }
        } else {
            val returnCursor: Cursor? = context.contentResolver.query(uri, null,
                null, null, null)
            if (returnCursor != null) {
                val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                returnCursor.moveToFirst()
                filename = returnCursor.getString(nameIndex)
                returnCursor.close()
            }
        }

        return filename
    }

    /**
     * Get the file name from a Uri.
     *
     * @param filename The full name of a file.
     * @return The file name.
     */
    fun getName(filename: String?): String? {
        if (filename == null) {
            return null
        }
        val index: Int = filename.lastIndexOf('/')
        return filename.substring(index + 1)
    }

    private fun getGoogleDriveFilePath(uri: Uri, context: Context): String {
        var returnUri = uri
        var returnCursor: Cursor? = context.contentResolver.query(returnUri, null, null, null, null)
        /*
         * Get the column indexes of the data in the Cursor,
         * move to the first row in the Cursor, get the data,
         * and display it.
         */
        val nameIndex: Int = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex: Int = returnCursor!!.getColumnIndex(OpenableColumns.SIZE)
        returnCursor!!.moveToFirst()

        val name: String = (returnCursor!!.getString(nameIndex))
        val size: String = (returnCursor!!.getLong(sizeIndex)).toString()
        val file = File(context.cacheDir, name)
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            var read: Int
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable: Int = inputStream?.available() ?: 0
            val bufferSize: Int = Math.min(bytesAvailable, maxBufferSize)

            val buffers = ByteArray(bufferSize)
            while (inputStream?.read(buffers).also { read = it!! } != -1) {
                outputStream.write(buffers, 0, read)
            }
            inputStream?.close()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file.path
    }
}


/**
 * Get a file path from a Uri. This will get the path for Storage Access
 * Framework Documents, as well as the _data field for the MediaStore and
 * other file-based ContentProviders.<br></br>
 * <br></br>
 * Callers should check whether the path***/