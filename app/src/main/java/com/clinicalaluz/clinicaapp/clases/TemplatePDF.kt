package com.clinicalaluz.clinicaapp.clases

//import android.media.Image


import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import com.clinicalaluz.clinicaapp.PrincipalActivity
import com.clinicalaluz.clinicaapp.R
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class TemplatePDF(private  val context: Context) {
    private  var pdfFile : File?=null
    private var document : Document?=null
    private var pdfWriter:PdfWriter?=null
    private var paragraph:Paragraph?=null


    fun openDocument(){
        createFile()
        try {
            document= Document(PageSize.A4)
            pdfWriter= PdfWriter.getInstance(document,FileOutputStream(pdfFile))
            document!!.open()

        }catch (ex:Exception){

        }
    }

    private fun createFile() {
        val folder =File(Environment.getExternalStorageDirectory().toString(),"ClinicaLaLuz")
        if (!folder.exists())
            folder.mkdirs()
        pdfFile=File(folder,"Reporte-"+getCurrentDate()+".pdf")
    }
    fun getCurrentDate():String{
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        return sdf.format(Date())
    }
    fun addMetaData(title:String?,subject:String?,author:String){
        document!!.addTitle(title)
        document!!.addSubject(subject)
        document!!.addAuthor(author)
    }

    private  fun addChilP(chilParagraph: Paragraph){
        chilParagraph.alignment=Element.ALIGN_CENTER
        paragraph!!.addAll(chilParagraph)
    }

    fun addTitles(title:String?,subTitle:String?,date:String){
        try {
            paragraph= Paragraph()
            paragraph!!.spacingAfter=5f
            paragraph!!.spacingBefore=5f
            document!!.add(paragraph)
        }
        catch (ex:Exception){

        }
    }
    fun addParagraph(text: String?) {
        try {
            paragraph = Paragraph(text)
            paragraph!!.spacingBefore = 5f
            paragraph!!.spacingAfter = 5f
            document!!.add(paragraph)
        } catch (ex: Exception) {
        }
    }

    fun  imgeset(){
        var drawable:Drawable

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            drawable = context.getResources().getDrawable(R.drawable.logoclinica, null);
        }
        else
        {
            drawable = context.getResources().getDrawable(R.drawable.logoclinica);
        }

//        var bitmapss:Bitmap
//        var bitmap = (drawable as BitmapDrawable).bitmap
//        val stream:ByteArrayInputStream
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

//        val bitmap = (drawable as BitmapDrawable).bitmap
//        val stream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
//        val bitmapData = stream.toByteArray()
//
//        val imageData: ImageDa = ImageDataFactory.create(bitmapData)
//        val image = android.media.Image(stream.toByteArray())


        //val drawable = ContextCompat.getDrawable(context, R.mipmap.logoclinica)
        val bmp = (drawable as BitmapDrawable?)!!.bitmap

        val stream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val logo: Image = Image.getInstance(stream.toByteArray())
      //  val image = android.media.Image(stream.toByteArray())
        logo.setWidthPercentage(80f)
        logo.scaleToFit(90f, 90f)
        //logo.height?=100

//        val logoCell = PdfPCell(logo)
//
//
//        paragraph = Paragraph(logoCell)
//        paragraph!!.spacingBefore = 5f
//        paragraph!!.spacingAfter = 5f
//        document!!.add(paragraph)
        document!!.add(logo)


    }
    fun createTable(header :Array<String>,listaTest:ArrayList<ClsTest>){
        try {
            paragraph= Paragraph()
            val pdfTable=PdfPTable(header.size)
            pdfTable.widthPercentage=100f
            var pdfCell:PdfPCell
            var pdfPCell: PdfPCell
            var indexC=0
            while (indexC < header.size) {
                //  val d=Phrase(header[indexC++],)
                pdfPCell = PdfPCell(Phrase(header[indexC++]))
                pdfPCell.horizontalAlignment = Element.ALIGN_CENTER
                pdfPCell.backgroundColor = BaseColor.RED
                pdfTable.addCell(pdfPCell)
            }
            val tabla = PdfPTable(4)
            for(test in  listaTest)
            {
                pdfPCell = PdfPCell(Phrase(test.FECHA))
                pdfPCell.horizontalAlignment = Element.ALIGN_CENTER
                pdfTable.addCell(pdfPCell)
                pdfPCell = PdfPCell(Phrase(test.SISTOLICA))
                pdfPCell.horizontalAlignment = Element.ALIGN_CENTER
                pdfTable.addCell(pdfPCell)
                pdfPCell = PdfPCell(Phrase(test.DIASTOLICA))
                pdfPCell.horizontalAlignment = Element.ALIGN_CENTER
                pdfTable.addCell(pdfPCell)
                pdfPCell = PdfPCell(Phrase(test.PULSO))
                pdfPCell.horizontalAlignment = Element.ALIGN_CENTER
                pdfTable.addCell(pdfPCell)

            }
//            for (i in 0..14) {
//                pdfTable.addCell("celda $i")
//            }
            paragraph?.add(pdfTable)
            //paragraph!!.add(tabla)
            document!!.add(paragraph)

        }catch (ex:Exception){
        }
    }
    fun viewPdf(){
       // var intent=
//        val intent = Intent(context, PrincipalActivity::class.java)
//        intent.putExtra("path", "dni")
//        context.startActivity(intent)
       // finish()

        val packageManager = context.packageManager
        val testIntent = Intent(Intent.ACTION_VIEW)
        //testIntent.setType("application/pdf")
        testIntent.type="application/pdf"
        val list: List<*> =
            packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY)
        if (list.size > 0) {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
         //   val uri: Uri = Uri.fromFile(pdfFile)
            if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.N) {
                val uri: Uri = Uri.parse(pdfFile.toString())
                intent.setDataAndType(uri, "application/pdf")
                context.startActivity(intent)
            } else{
                val uri: Uri = Uri.fromFile(pdfFile)
                intent.setDataAndType(uri, "application/pdf")
                context.startActivity(intent)
            }

        } else {
            Toast.makeText(
                context,
                "Download a PDF Viewer to see the generated PDF",
                Toast.LENGTH_SHORT
            ).show()
        }

    }
    fun closeDocument(){
        document!!.close()
    }
}