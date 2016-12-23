package hentati.nejmeddine.saisieexamens;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by feres on 02/12/2016.
 */
public class BaseActivity extends AppCompatActivity {


    private ProgressDialog mProgressDialog;


    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }


        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    public boolean checkConnection() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();

        if(activeNetworkInfo != null){
            return activeNetworkInfo.isConnected();
        }
        return false;

    }



    public void generateFile() {


        //Open file path
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(WorkbookUtil.createSafeSheetName(
                ExamensHelper.getInstance(this).getmEnseignant().getCin()
        ));

        sheet.setDefaultColumnWidth(20);

        String firstCell = "Professeur : "+ExamensHelper.getInstance(this).getmEnseignant().getCin()+"\n"
                + "Année : "+ExamensHelper.getInstance(this).getProfLevel()+"\n"
                + "Module : "+ExamensHelper.getInstance(this).getProfSubject();
        if(!ExamensHelper.getInstance(this).getProfModule().equals("")) {

            firstCell += "\nMatière : "+ExamensHelper.getInstance(this).getProfModule();
        }

        //first line to display the level and the subject
        Row row = sheet.createRow(0);
        createCell(workbook, row, (short) 0, CellStyle.ALIGN_LEFT, firstCell, false);


        //create an empty line
        sheet.createRow(1);


        //second line to display cells titles
        row = sheet.createRow(2);

        createCell(workbook, row, (short) 0, CellStyle.ALIGN_CENTER, "Code de l'étudiant", true);

        createCell(workbook, row, (short) 1, CellStyle.ALIGN_CENTER, "ECRIT", true);

        createCell(workbook, row, (short) 2, CellStyle.ALIGN_CENTER, "ORAL", true);

        createCell(workbook, row, (short) 3, CellStyle.ALIGN_CENTER, "TP", true);



        //main content to display the data
        for (int i=0;i<ExamensHelper.getInstance(this).getmNotesList().size();i++) {
            row = sheet.createRow(i+3);

            createCell(workbook, row, (short) 0, CellStyle.ALIGN_CENTER
                    , ExamensHelper.getInstance(this).getmNotesList().get(i).getCode(), false);

            if(ExamensHelper.getInstance(this).getmNotesList().get(i).getEcrit() != null) {
                createCell(workbook, row, (short) 1, CellStyle.ALIGN_CENTER
                        , ExamensHelper.getInstance(this).getmNotesList().get(i).getEcrit(), false);
            }

            if(ExamensHelper.getInstance(this).getmNotesList().get(i).getOral() != null) {
                createCell(workbook, row, (short) 2, CellStyle.ALIGN_CENTER
                        , ExamensHelper.getInstance(this).getmNotesList().get(i).getOral(), false);
            }

            if(ExamensHelper.getInstance(this).getmNotesList().get(i).getTp() != null) {
                createCell(workbook, row, (short) 3, CellStyle.ALIGN_CENTER
                        , ExamensHelper.getInstance(this).getmNotesList().get(i).getTp(), false);
            }


        }


        //file name
        String outFileName = ExamensHelper.getInstance(this).getProfLevel()+"-"
                + ExamensHelper.getInstance(this).getProfSubject();
        if(!ExamensHelper.getInstance(this).getProfModule().equals(""))
            outFileName += "-"+ExamensHelper.getInstance(this).getProfModule();
        outFileName += ".xlsx";

        //path directory
        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/saisie examens";


        try {
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            //printlnToUser("writing file " + outFileName);
            //File cacheDir = getCacheDir();
            File file = new File(fullPath, outFileName);
            if(file.exists())
                file.delete();
            file.createNewFile();
            OutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            //printlnToUser("saved to : "+file);
            outputStream.flush();
            outputStream.close();


            Toast.makeText(getApplicationContext(), "Fichier Excel généré.", Toast.LENGTH_SHORT).show();
            //printlnToUser("sharing file...");
            //share(outFileName, getApplicationContext());
        } catch (Exception e) {
            /* proper exception handling to be here */
            //printlnToUser(e.toString());
            Toast.makeText(getApplicationContext(), "Erreur ! veuillez donner la permission d'écriture et de lecture à l'application svp !", Toast.LENGTH_SHORT).show();

        }

    }

    public static void createCell(XSSFWorkbook wb, Row row, short column, short halign, String value, Boolean boldFont) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(halign);
        Font f = wb.createFont();
        f.setBold(boldFont);
        cellStyle.setFont(f);
        cell.setCellStyle(cellStyle);
    }


    public void openFolder(){
        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/saisie examens/";
        File file = new File(fullPath);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "*/*");
        startActivity(Intent.createChooser(intent, "Choisissez votre explorateur de fichiers"));
    }

}

