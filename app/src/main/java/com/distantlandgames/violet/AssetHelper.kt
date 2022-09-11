package com.distantlandgames.violet.helpers

import android.content.Context
import android.content.res.Resources
import android.opengl.GLES20
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import android.opengl.GLUtils
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import java.io.FileReader

class AssetHelper {
    companion object {
        fun loadStringFromAssetFile(context: Context, filePath: String): String {
            val shaderSource = StringBuilder()
            try {
                // Option 1:
                val reader = BufferedReader(FileReader(filePath))

                // Option 2:
               // val reader = BufferedReader(InputStreamReader(context.assets.open(filePath)))
                Log.e("violet", "Trying to open file: ${filePath}")
                var line: String

                do {
                    line = reader.readLine()
                    shaderSource.append(line).append("\n")
                }while(line != null)

                reader.close()
                return shaderSource.toString()
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("violet", "Could not load shader file")
                return ""
            }

        }

        fun readTextFileFromRawResource(context: Context, resource: Int): String {
            val body = StringBuilder()
            try {
                val inputStream = context.getResources().openRawResource(resource)
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)

                var nextLine: String?

                //body.append("'")
                do {
                    nextLine = bufferedReader.readLine()
                    if(nextLine != null) {
                        body.append(nextLine)

                    }
                    body.append('\n')
                    //if(nextLine == null)
                    //    body.append("'")
                } while (nextLine != null)

            } catch (ioe: IOException) {
                throw RuntimeException("Could not open resource: " + resource, ioe)
            } catch (nfe: Resources.NotFoundException) {
                throw RuntimeException("Resource not found: " + resource, nfe)
            }

            return body.toString()
        }

        fun loadTexture(context: Context, resourceId: Int): Int {
            val textureHandle = IntArray(1)

            GLES20.glGenTextures(1, textureHandle, 0)

            if (textureHandle[0] != 0) {
                val options = BitmapFactory.Options()
                options.inScaled = false   // No pre-scaling

                // Read in the resource
                /**
                Get the texture in a format that OpenGL will understand. We can’t just feed it
                raw data from a PNG or JPG, because it won’t understand that. The first step that
                we need to do is to decode the image file into an Android Bitmap object:
                 */
                val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)

                if(bitmap == null) {
                    throw RuntimeException("Decoding bitmap failed.")
                }

                /**
                By default, Android applies pre-scaling to bitmaps depending on the resolution of your
                device and which resource folder you placed the image in. We don’t want Android to scale
                our bitmap at all, so to be sure, we set inScaled to false.
                 */
                // Bind to the texture in OpenGL
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])

                // Set filtering
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST)
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST)

                // Load the bitmap into the bound texture.
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

                //GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, bitmap);

                // Recycle the bitmap, since its data has been loaded into OpenGL.
                /**
                We then call recycle() on the original bitmap, which is an important step to
                free up memory. The texture has been loaded into OpenGL, so we don’t need to
                keep a copy of it lying around. Yes, Android apps run under a Dalvik VM that
                performs garbage collection, but Bitmap objects contain data that resides in
                native memory and they take a few cycles to be garbage collected if you don’t
                recycle them explicitly. This means that you could actually crash with an out
                of memory error if you forget to do this, even if you no longer hold any references to the bitmap.
                 */
                bitmap.recycle()
            }

            if (textureHandle[0] == 0) {
                throw RuntimeException("Error loading texture.")
            }

            return textureHandle[0]
        }

        //fun createResourceType(context: Context, resourceId: Int): ResourceTexture {
        //    return ResourceTexture(loadTexture(context, resourceId))
        //}
    }
}