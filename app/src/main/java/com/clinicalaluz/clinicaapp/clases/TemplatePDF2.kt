package com.clinicalaluz.clinicaapp.clases

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Environment
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

//
//class TemplatePDF2 {
//}



class TemplatePDF2(private  val context: Context) {
    private  var pdfFile : File?=null
    private var document : Document?=null
    private var pdfWriter: PdfWriter?=null
    private var paragraph: Paragraph?=null


    fun openDocument(){
        createFile()
        try {
            document= Document(PageSize.A4)
            pdfWriter= PdfWriter.getInstance(document, FileOutputStream(pdfFile))
            document!!.open()

        }catch (ex:Exception){

        }
    }

    private fun createFile() {
        val folder = File(Environment.getExternalStorageDirectory().toString(),"ClinicaLaLuz")
        if (!folder.exists())
            folder.mkdirs()
        pdfFile= File(folder,"ReporteGlucosa-"+getCurrentDate()+".pdf")
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
        chilParagraph.alignment= Element.ALIGN_CENTER
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
        var drawable: Drawable

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
    fun createTable(header :Array<String>,listaTest: ArrayList<ClsTest2>){
        try {
            paragraph= Paragraph()
            val pdfTable= PdfPTable(header.size)
            pdfTable.widthPercentage=100f
            var pdfCell: PdfPCell
            var pdfPCell: PdfPCell
            var indexC=0
            while (indexC < header.size) {
                pdfPCell = PdfPCell(Phrase(header[indexC++]))
                pdfPCell.horizontalAlignment = Element.ALIGN_CENTER
                pdfPCell.backgroundColor = BaseColor.RED
                pdfTable.addCell(pdfPCell)
            }
            val tabla = PdfPTable(2)
            for(test in  listaTest)
            {
                pdfPCell = PdfPCell(Phrase(test.FECHA))
                pdfPCell.horizontalAlignment = Element.ALIGN_CENTER
                pdfTable.addCell(pdfPCell)
                pdfPCell = PdfPCell(Phrase(test.REG_AZUCAR))
                pdfPCell.horizontalAlignment = Element.ALIGN_CENTER
                pdfTable.addCell(pdfPCell)

            }

            paragraph?.add(pdfTable)
            //paragraph!!.add(tabla)
            document!!.add(paragraph)

        }catch (ex:Exception){
        }
    }
    fun closeDocument(){
        document!!.close()
    }
}