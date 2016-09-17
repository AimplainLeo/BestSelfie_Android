package shadasviar.bestselfie.PictureStorage.Share;

import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import shadasviar.bestselfie.BestSelfie;
import shadasviar.bestselfie.PictureStorage.HistoryManager.HistoryManager;
import shadasviar.bestselfie.PictureStorage.HistoryManager.IHistoryManager;


/**
 * Created by vlad on 8/6/16.
 * @author Shadasviar
 * This class share given list of pictures and add it to SENT history
 */
public class Share implements IShare {


    @Override
    public void share(List<File> data) {

        ArrayList<Uri> toShare = new ArrayList<>();
        for(File f : data){
            toShare.add(Uri.fromFile(f));
        }


        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, toShare);
        shareIntent.setType("image/*");

        Intent myChooser =Intent.createChooser(shareIntent, "Share with:");
        myChooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        BestSelfie.getContext().startActivity(myChooser);

        IHistoryManager hm = HistoryManager.getInstance();
        hm.addToHistory(IHistoryManager.HistoryTypes.SENT, data);
    }

}
