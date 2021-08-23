package com.example.blocodenotas

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.blocodenotas.model.notepad
import kotlinx.android.synthetic.main.activity_detalhes.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.save_dialog.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class NewNoteActivity : AppCompatActivity() {

    private var savedImage: Uri? = null


    companion object {

        private const val CAMERA_PERMISSION_CODE = 1
        private const val STORAGE_PERMISSION_CODE = 2
        private const val IMAGE_DIRECTORY ="NotePad"


    }


    val dbHandler=DbHandler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        nv.setOnNavigationItemSelectedListener {

           when (it.itemId){
               R.id.action_list-> {
                   val intent= Intent(this,ListActivity::class.java)
                  startActivity(intent)
                   return@setOnNavigationItemSelectedListener true
               }

               R.id.action_save->{

                   if(et.text.isEmpty())
                   {
                       Toast.makeText(this,"Bloco vazio",Toast.LENGTH_SHORT).show()
                   }
                   else {
                       showSaveDialog()

                   }


                   return@setOnNavigationItemSelectedListener true
               }
               else ->  return@setOnNavigationItemSelectedListener false
           }

        }

    }

    private fun showSaveDialog() {
         val cal = Calendar.getInstance()
        val sdf=SimpleDateFormat("dd/MM/yyyy")
       val date= sdf.format(cal.time)


        val dialog=Dialog(this)
        dialog.setContentView(R.layout.save_dialog)
        dialog.btn_save.setOnClickListener {


            val title=dialog.et_nome.text.toString()
            val sa=SharedAnottation(this)
            val bn = et.text.toString()

           if (savedImage==null)
           {
               val notepad=notepad(0,title,"",bn,date)
               dbHandler.addNote(notepad)
           }
            else
           {
               val notepad=notepad(0,title,savedImage.toString(),bn,date)
               dbHandler.addNote(notepad)
           }

      
            dialog.dismiss()
            Toast.makeText(this,"Salvo com sucesso",Toast.LENGTH_SHORT).show()
            finish()
        }
        dialog.btn_cancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()



    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_main,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.action_erase)
        {
            et.text.clear()
        }
        if (item.itemId==R.id.action_choose_from_gallery)
        {
            reqstPermissionsForGllry()

        }
        if (item.itemId==R.id.action_take_photo)
        {
            reqstPermissionsForCamera()
        }

        return super.onOptionsItemSelected(item)
    }



    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {

        val wrapper= ContextWrapper(this)
        var file= wrapper.getDir(NewNoteActivity.IMAGE_DIRECTORY,Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {

            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }


        return Uri.parse(file.absolutePath)
    }

    private  fun reqstPermissionsForGllry() {

        if (ContextCompat.checkSelfPermission(this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA
            ).toString()) == PackageManager.PERMISSION_GRANTED)
        {

            val galerryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galerryIntent, NewNoteActivity.STORAGE_PERMISSION_CODE)

        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                NewNoteActivity.STORAGE_PERMISSION_CODE
            )
        }

    }

    private  fun reqstPermissionsForCamera() {

        if(ContextCompat.checkSelfPermission(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA).toString())==PackageManager.PERMISSION_GRANTED)
        {
            val cameraIntent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, NewNoteActivity.CAMERA_PERMISSION_CODE)
        }
        else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA), NewNoteActivity.CAMERA_PERMISSION_CODE
            )
        }


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)



        if( grantResults.isNotEmpty() && grantResults[0] + grantResults[1] +grantResults[2]==PackageManager.PERMISSION_GRANTED)
        {
            if (requestCode== NewNoteActivity.STORAGE_PERMISSION_CODE)
            {
                val galerryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galerryIntent, NewNoteActivity.STORAGE_PERMISSION_CODE)
            }
            if (requestCode== NewNoteActivity.CAMERA_PERMISSION_CODE)
            {
                val cameraIntent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, NewNoteActivity.CAMERA_PERMISSION_CODE)
            }
        }
        else
        {
            Toast.makeText(this, "Permissão negada", Toast.LENGTH_LONG).show()
        }







    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null && requestCode == NewNoteActivity.STORAGE_PERMISSION_CODE) {

            val contentUri = data.data
            if(Build.VERSION.SDK_INT < 28) {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentUri)
                iv_new_note.visibility = View.VISIBLE
                iv_new_note.setImageBitmap(bitmap)
                savedImage=saveImageToInternalStorage(bitmap)
            } else {
                val source = contentUri?.let { ImageDecoder.createSource(this.contentResolver, it) }
                val bitmap = source?.let { ImageDecoder.decodeBitmap(it) }
                iv_new_note.visibility = View.VISIBLE
                iv_new_note.setImageBitmap(bitmap)
                savedImage= bitmap?.let { saveImageToInternalStorage(it) }
            }


//            val selectedImage = MediaStore.Images.Media.getBitmap(this.contentResolver, contentUri)
//            iv_detalhes.visibility = View.VISIBLE
//            iv_detalhes.setImageBitmap(selectedImage)


        }

        if (resultCode== Activity.RESULT_OK && data !=null && requestCode== NewNoteActivity.CAMERA_PERMISSION_CODE)
        {
            val foto:Bitmap= data!!.extras!!.get("data") as Bitmap
            iv_new_note.visibility = View.VISIBLE
            iv_new_note.setImageBitmap(foto)
            savedImage=saveImageToInternalStorage(foto)
        }


    }




}