package shadasviar.bestselfie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import shadasviar.bestselfie.PictureSelector.PictureSelector;
import shadasviar.bestselfie.PictureSelector.PictureSelectorInterface;
import shadasviar.bestselfie.PictureStorage.DataProviders.AllData;
import shadasviar.bestselfie.PictureStorage.DataProviders.HistoryData;
import shadasviar.bestselfie.PictureStorage.DataProviders.IDataStorage;
import shadasviar.bestselfie.PictureStorage.HistoryManager.HistoryManager;
import shadasviar.bestselfie.PictureStorage.IDataStorageManager;
import shadasviar.bestselfie.PictureStorage.HistoryManager.IHistoryManager;
import shadasviar.bestselfie.PictureStorage.Share.IShare;
import shadasviar.bestselfie.PictureStorage.PictureStorage;
import shadasviar.bestselfie.PictureStorage.Share.Share;

public class MainActivity extends AppCompatActivity {

    private IDataStorageManager storage;
    private List<File> pictures = new ArrayList<>();
    private List<File> limitedPictures = new ArrayList<>();
    private ImageAdapter imageAdapter;
    private List<Bitmap> bitmapList = new ArrayList<>();
    private int minNewPicturesForAutoUpdatingView = 100;

    @Override
     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storage = new PictureStorage();
        IDataStorage ds = new HistoryData(IHistoryManager.HistoryTypes.USEFUL);
        pictures = ds.getActualData();

        final GridView gridview = (GridView) findViewById(R.id.gridView);
        imageAdapter = new ImageAdapter(this);
        gridview.setAdapter(imageAdapter);

        /*thread witch cache data*/
        Thread thread = new Thread(new Runnable() {
            @Override
            synchronized public void run() {
                PictureSelectorInterface p = new PictureSelector();

                while(true) {
                    if(p.cacheData(getNewPictures())){
                        updatePictures();
                    }

                    try {
                        TimeUnit.SECONDS.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        thread.start();

        /*thread whitch update data and show it*/

        Thread mt = new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                while (getNewPictures().size() > minNewPicturesForAutoUpdatingView) {
                    try {
                        TimeUnit.SECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updatePictures();
                }
            }
        });
        mt.start();



    }

    List<File> getNewPictures(){
        IDataStorage ds = new AllData();

        List<File> data = new ArrayList<>(ds.getActualData());

        ds = new HistoryData(IHistoryManager.HistoryTypes.CACHE);
        data.removeAll(ds.getActualData());

        return data;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_share:
                share();
                return true;

            case R.id.action_show_actual_data:
                dataf();
                check(item);
                return true;

            case R.id.action_show_history:
                check(item);
                history();
                return true;

            case R.id.action_clear_history:
                clear();
                return true;

            case R.id.action_exit:
                System.exit(0);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void check(MenuItem item){
        if (item.isChecked()){
            item.setIcon(R.drawable.ic_unchecked);
            item.setChecked(false);
        }
        item.setChecked(true);
        item.setIcon(R.drawable.ic_action_name);
    }

/*
    public synchronized void showNextPicture(View view){
        ImageView im = (ImageView) findViewById(R.id.imageView);


        PictureSelectorInterface p = new PictureSelector();
        pictures = storage.getActualData();


        if (index >= pictures.size()) index = 0;
        if(pictures.size() > 0) {
            im.setImageURI(Uri.parse(pictures.get(index++).toString()));
        }

    }
    */


    public void updatePictures(){
        PictureSelectorInterface p = new PictureSelector();
        pictures = storage.getActualData();
        limitedPictures = p.getFilteredPictures(pictures);
        checkFace();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageAdapter.notifyDataSetChanged();

                final GridView gridview = (GridView) findViewById(R.id.gridView);
                gridview.invalidateViews();
            }
        });
    }


    private void clear(){
        IHistoryManager hm = HistoryManager.getInstance();
        hm.clearHistory(IHistoryManager.HistoryTypes.SENT);
        updatePictures();
    }


    private void history(){
        storage.setHistoryData(IHistoryManager.HistoryTypes.SENT);
        updatePictures();
    }


    private void share(){
        IShare s = new Share();
        s.share(limitedPictures);
        updatePictures();
        saveToDir();
    }


    private void dataf(){
        storage.setBasedOnHistoryData(IHistoryManager.HistoryTypes.SENT);
        updatePictures();
    }


    public class ImageAdapter extends BaseAdapter{

        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        @Override
        public synchronized int getCount() {
            return bitmapList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public synchronized View getView(int i, View view, ViewGroup viewGroup) {
            ImageView imageView;
            if (view == null) {
                imageView = new ImageView(mContext);
                //imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) view;
            }

            try {
                if (bitmapList.get(i) != null) {
                    Bitmap bmp = Bitmap.createScaledBitmap(bitmapList.get(i), 800, 800, false);
                    imageView.setImageBitmap(bmp);
                }
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
                System.out.println("Current index: "+ i +" size: " + bitmapList.size());
            }
            return imageView;
        }



    }


    /*Method only for testing how faces are detected*/
    synchronized void checkFace(){
        int k = 0;
        int maxFaces = 1;

        bitmapList.clear();

        for(int i = 0; i < 10; ++i){

            BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();
            BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(pictures.get(k++).getPath(), BitmapFactoryOptionsbfo);


                FaceDetector detector = new FaceDetector(bitmap.getWidth(), bitmap.getHeight(), maxFaces);
                FaceDetector.Face[] faces = new FaceDetector.Face[maxFaces];

                int numFaces = detector.findFaces(bitmap, faces);


                if(numFaces > 0){
                    Bitmap mutable = bitmap.copy(Bitmap.Config.RGB_565, true);
                    Canvas canvas = new Canvas(mutable);

                    Paint myPaint = new Paint();
                    myPaint.setColor(Color.GREEN);
                    myPaint.setStyle(Paint.Style.STROKE);
                    myPaint.setStrokeWidth(3);

                    FaceDetector.Face face = faces[0];
                    PointF myMidPoint = new PointF();
                    face.getMidPoint(myMidPoint);
                    float myEyesDistance = face.eyesDistance();

                    canvas.drawRect((int) (myMidPoint.x - myEyesDistance * 2),
                    (int) (myMidPoint.y - myEyesDistance * 2),
                    (int) (myMidPoint.x + myEyesDistance * 2),
                    (int) (myMidPoint.y + myEyesDistance * 2), myPaint);

                    bitmapList.add(mutable);
                }else{
                    --i;
                }
                if(k >= pictures.size()){
                    return;
                }
            }catch (IndexOutOfBoundsException e){
                return;
            }
        }
    }


    void saveToDir(){
        File dir = new File(Environment.getExternalStorageDirectory() + "/Detected");
        if(dir.exists()){

        }else{
            dir.mkdirs();
        }
        Integer i = 0;
        for(Bitmap b : bitmapList){
            File res = new File(dir, i.toString() + ".jpg");
            if (res.exists())
                res.delete();
            ++i;
            try {
                FileOutputStream out = new FileOutputStream(res);
                b.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
