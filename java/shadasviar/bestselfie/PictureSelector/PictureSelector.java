package shadasviar.bestselfie.PictureSelector;


import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.FaceDetector;
import android.support.v4.app.NotificationCompat;

import java.io.File;

import java.util.List;
import java.util.*;

import shadasviar.bestselfie.BestSelfie;
import shadasviar.bestselfie.PictureStorage.HistoryManager.HistoryManager;
import shadasviar.bestselfie.PictureStorage.HistoryManager.IHistoryManager;
import shadasviar.bestselfie.R;

import static java.lang.Math.pow;


/**
 * Created by vlad on 7/30/16.
 */
public class PictureSelector implements PictureSelectorInterface {

    /*TODO: reproject how program will take max nuber of faces*/
    int maxFaces = 1; /*Tmp variable for testing*/

    @Override
    public List<File> getFilteredPictures(List<File> data) {

        int limit = 10;

        if(data.size() < limit) return data;
        List<File> result = new ArrayList<>();
        for(int i = 0; i < limit; ++i){
            result.add(i, data.get(i));
        }
        return result;
    }


    /*All paths to pictures witch were checked are written to CACHE history to
    * avoiding checking whole FS every time when program starts. All cashed paths will not checked
    * if it isn't face. Photos with faces will cashed to USEFUL cash and program will use only
    * thees useful paths.*/
    @Override
    public synchronized boolean cacheData(List<File> data) {

        if(data.size() == 0) return false;

        final int oneTimeLimit= 10;

        List<File> useful = new ArrayList<>(oneTimeLimit);
        List<File> cache = new ArrayList<>(oneTimeLimit);

        /*useful cache tmp counter*/
        int i = 0;
        /*whole cache tmp counter*/
        int count = 0;

        /*Creating notification*/
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(BestSelfie.getContext())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Scanning photos")
                        .setContentText("Scanning photos is in progress...");
        NotificationManager mManager = (NotificationManager) BestSelfie.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        /*counter of progress*/
        Integer ntr = 0;
        /*id of notification*/
        int id = 0;

        for(File f : data) {

            /*Notification showing*/
            mBuilder.setProgress(data.size(), ntr++, false)
                .setContentText(ntr.toString() + " / " + data.size());
            mManager.notify(id, mBuilder.build());

            /*Pictures are written to history by 10 for decrease number
            * of writes to memory and increase performance*/
            if(i >= oneTimeLimit-1){
                i = addToUseful(useful);
            }

            try {
                BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();
                BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;
                Bitmap bitmap = BitmapFactory.decodeFile(f.getPath(), BitmapFactoryOptionsbfo);

                FaceDetector detector = new FaceDetector(bitmap.getWidth(), bitmap.getHeight(), maxFaces);
                FaceDetector.Face[] faces = new FaceDetector.Face[maxFaces];

                int numFaces = detector.findFaces(bitmap, faces);

                if(numFaces > 0){
                    if(faceIsPretty(faces[0], bitmap)) {
                        useful.add(f);
                        ++i;
                    }
                }
            }catch (Exception ex){
                System.out.println(ex.getMessage());
            }

            cache.add(f);
            if(count++ >= oneTimeLimit-1){
                IHistoryManager hm = HistoryManager.getInstance();
                hm.addToHistory(IHistoryManager.HistoryTypes.CACHE, cache);
                cache.clear();
                count = 0;
            }

        }

        IHistoryManager hm = HistoryManager.getInstance();
        hm.addToHistory(IHistoryManager.HistoryTypes.CACHE, cache);

        if(i > 0){
            addToUseful(useful);
        }
        /*hide notification*/
        mManager.cancel(id);
        return true;
    }


    private int addToUseful(List<File> list){
        IHistoryManager hm = HistoryManager.getInstance();
        hm.addToHistory(IHistoryManager.HistoryTypes.USEFUL, list);
        list.clear();
        return 0;
    }


    private boolean faceIsPretty(FaceDetector.Face face, Bitmap bitmap){
        float facePercent = 0f;
        /*Square of face*/
        facePercent = (float) Math.pow(face.eyesDistance() * 4, 2);
        /*Square of picture*/
        float pictureSquare = bitmap.getWidth() * bitmap.getHeight();
        /*Square of face in percents*/
        facePercent = (facePercent/pictureSquare) * 100;

        if(face.confidence() > 0.4 && facePercent > 10)return true;
        return false;
    }

}
