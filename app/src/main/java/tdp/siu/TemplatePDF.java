package tdp.siu;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;

public class TemplatePDF extends FileProvider {
    private Context context;
    private File pdfFile;
    private Document document;
    private PdfWriter pdfWriter;
    private Paragraph paragraph;
    private Font fTitle = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
    private Font fSubtitle = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
    private Font fText = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
    private Font fHighText = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD);

    public TemplatePDF(){

    }

    public TemplatePDF (Context context) {
        this.context = context;
    }

    public void openDocument(){
        createFile();
        try{

            document = new Document(PageSize.A4);
            pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

        } catch (Exception e ){
            Log.e("openDocument", e.toString());
        }
    }

    private void createFile(){
        File folder = new File(Environment.getExternalStorageDirectory().toString(), "PDF");

        if(!folder.exists()){
            folder.mkdirs();
        }
        pdfFile = new File(folder, "Template.pdf");
    }

    public void addMetaData(String title, String subject, String author){
        document.addTitle(title);
        document.addSubject(subject);
        document.addAuthor(author);
    }

    public void addTitles(String title, String subtitle, String date){
        paragraph = new Paragraph();
        addChildP(new Paragraph(title, fTitle));
        addChildP(new Paragraph(subtitle, fSubtitle));
        addChildP(new Paragraph("Fecha de generación del certificado: " + date, fHighText));
        paragraph.setSpacingAfter(30);
        try {
            document.add(paragraph);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void addChildP(Paragraph childParagraph){
        childParagraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.add(childParagraph);
    }

    public void addParagraph(String text){
        paragraph = new Paragraph(text, fText);
        paragraph.setSpacingAfter(5);
        paragraph.setSpacingBefore(5);
        try {
            document.add(paragraph);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public void closeDocument(){
        document.close();
    }

    public void viewPDF(Activity activity){
        if(pdfFile.exists()){
            //Uri uri = Uri.fromFile(pdfFile);
            Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".my.package.name.provider", pdfFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "application/pdf");
            try {
                activity.startActivity(intent);
            }catch (ActivityNotFoundException e){
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("marrket://details?id=com.adobe.reader")));
                Toast.makeText(activity.getApplicationContext(), "No hay aplicación para ver el PDF", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity.getApplicationContext(), "No existe el PDF", Toast.LENGTH_SHORT).show();
        }
    }

    public void addImage(Image image) {

        try {
            image.setAlignment(Element.ALIGN_CENTER);
            image.scalePercent(30);
            document.add(image);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }
}
