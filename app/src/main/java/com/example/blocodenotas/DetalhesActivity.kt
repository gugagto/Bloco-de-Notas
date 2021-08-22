package com.example.blocodenotas

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import com.example.blocodenotas.model.notepad
import kotlinx.android.synthetic.main.activity_detalhes.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class DetalhesActivity : AppCompatActivity() {

    val dbHandler = DbHandler(this)
    private lateinit var model: notepad
    private var savedImage: Uri? = null



    companion object {

        private const val CAMERA_PERMISSION_CODE = 1
        private const val STORAGE_PERMISSION_CODE = 2
        private const val IMAGE_DIRECTORY = "NotePad"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes)
        window.statusBarColor = Color.BLACK

        setSupportActionBar(tb_detalhes)
        val actionBar = supportActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        tb_detalhes.setNavigationOnClickListener {
            saveNote()
            onBackPressed()

        }

        if (intent.hasExtra("np")) {
            model = intent.getParcelableExtra<notepad>("np")
            actionBar?.setTitle(model.title)
            et_detalhes.setText(model.note)



            if (model.image.isNullOrEmpty())
            {

                iv_detalhes.visibility=View.GONE
            }
            else
            {
                savedImage= Uri.parse(model.image)
                iv_detalhes.visibility=View.VISIBLE
                iv_detalhes.setImageURI(Uri.parse(model.image))
            }


        }

        iv_detalhes.setOnLongClickListener {

            val dialog= AlertDialog.Builder(this)
            dialog.setMessage("Excluir foto?")
            dialog.setPositiveButton("Sim"){dialog, which ->


                savedImage=null
                iv_detalhes.visibility=View.GONE


            }
            dialog.setNegativeButton("Cancelar"){dialog, which ->

                dialog.dismiss()

            }
            dialog.show()

            return@setOnLongClickListener true

        }


    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detalhes, menu)
        return super.onCreateOptionsMenu(menu)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.action_erase) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Bloco de Notas")
            builder.setMessage("Deseja apagar ?")
            builder.setPositiveButton("Sim") { dialog, wich ->
                dialog.dismiss()
                dbHandler.delete(model)
                finish()

            }
            builder.setNegativeButton("Cancelar") { dialog, wich ->
                dialog.dismiss()
            }

            val alertDialog = builder.create()
            alertDialog.show()
        }



        if (item.itemId == R.id.action_choose_from_gallery) {

            reqstPermissionsForGllry()


        }

        if (item.itemId==R.id.action_take_photo)
        {
            reqstPermissionsForCamera()
        }


        return super.onOptionsItemSelected(item)


    }



    private fun saveNote() {
        val text = et_detalhes.text

        if(savedImage==null)
        {
            dbHandler.editNote(model, text.toString(),"")
        }
        else{
            dbHandler.editNote(model, text.toString(),savedImage.toString())
        }




    }

  private  fun reqstPermissionsForCamera() {

        if(ContextCompat.checkSelfPermission(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA).toString())==PackageManager.PERMISSION_GRANTED)
        {
            val cameraIntent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA_PERMISSION_CODE)
        }
        else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        }


    }

   private  fun reqstPermissionsForGllry() {

        if (ContextCompat.checkSelfPermission(this, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA
                ).toString()) == PackageManager.PERMISSION_GRANTED)
                {

            val galerryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galerryIntent, STORAGE_PERMISSION_CODE)

        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), STORAGE_PERMISSION_CODE)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)



       if( grantResults.isNotEmpty() && grantResults[0] + grantResults[1] +grantResults[2]==PackageManager.PERMISSION_GRANTED)
       {
           if (requestCode== STORAGE_PERMISSION_CODE)
           {
               val galerryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
               startActivityForResult(galerryIntent, STORAGE_PERMISSION_CODE)
           }
           if (requestCode==CAMERA_PERMISSION_CODE)
           {
               val cameraIntent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
               startActivityForResult(cameraIntent, CAMERA_PERMISSION_CODE)
           }
       }
        else
       {
           Toast.makeText(this, "Permissão negada", Toast.LENGTH_LONG).show()
       }


        

//        if (requestCode== STORAGE_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] + grantResults[1]
//            +grantResults[2]==PackageManager.PERMISSION_GRANTED)
//            {
//            val galerryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            startActivityForResult(galerryIntent, STORAGE_PERMISSION_CODE)
//        }
//
//
//        if (requestCode== CAMERA_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] + grantResults[1]
//        +grantResults[2]==PackageManager.PERMISSION_GRANTED)
//        {
//            val cameraIntent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            startActivityForResult(cameraIntent, CAMERA_PERMISSION_CODE)
//        }
//        else
//        {
//            Toast.makeText(this, "Permissão negada", Toast.LENGTH_LONG).show()
//        }


    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null && requestCode == STORAGE_PERMISSION_CODE) {

            val contentUri = data.data
            if(Build.VERSION.SDK_INT < 28) {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentUri)
                iv_detalhes.visibility = View.VISIBLE
                iv_detalhes.setImageBitmap(bitmap)
                    savedImage=saveImageToInternalStorage(bitmap)
            } else {
                val source = contentUri?.let { ImageDecoder.createSource(this.contentResolver, it) }
                val bitmap = source?.let { ImageDecoder.decodeBitmap(it) }
                iv_detalhes.visibility = View.VISIBLE
                iv_detalhes.setImageBitmap(bitmap)
                savedImage= bitmap?.let { saveImageToInternalStorage(it) }
            }


//            val selectedImage = MediaStore.Images.Media.getBitmap(this.contentResolver, contentUri)
//            iv_detalhes.visibility = View.VISIBLE
//            iv_detalhes.setImageBitmap(selectedImage)


        }

        if (resultCode==Activity.RESULT_OK && data !=null && requestCode== CAMERA_PERMISSION_CODE)
        {
            val foto:Bitmap= data!!.extras!!.get("data") as Bitmap
            iv_detalhes.visibility = View.VISIBLE
            iv_detalhes.setImageBitmap(foto)
            savedImage=saveImageToInternalStorage(foto)
        }


    }


    private fun saveImageToInternalStorage(bitmap: Bitmap ):Uri{

        val wrapper= ContextWrapper(this)
        var file= wrapper.getDir(IMAGE_DIRECTORY,Context.MODE_PRIVATE)
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


}