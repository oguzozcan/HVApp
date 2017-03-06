package com.armut.armuthv.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.armut.armuthv.MainActivity;
import com.armut.armuthv.R;
import com.armut.armuthv.components.TouchImageView;
import com.armut.armuthv.utils.ArmutUtils;
import com.armut.armuthv.utils.Constants;
import com.squareup.picasso.Picasso;

/**
 * Created by oguzemreozcan on 27/06/16.
 */
public class FullScreenImageAdapter extends PagerAdapter {

    private final Activity activity;
    private final String[] imagePaths;
//    private ArrayList<ImageInfo> images;
    private String image = "";
    private int[] screenSize;

    public FullScreenImageAdapter(Activity activity, String[] imagePaths, boolean profilePics) {
        this.activity = activity;
        this.imagePaths = imagePaths;
        screenSize = ArmutUtils.getScreenSize(activity);
        if(imagePaths != null){
            if(profilePics){
                //  image = new StringBuilder(Constants.IMAGE_BASE_URL_REAL).append("/").append(Constants.FULL_SIZE).append("/").append(Constants.FULL_SIZE)
                //       .append(Constants.OTHER_PICS_POSTFIX).toString();
                image = Constants.IMAGE_BASE_URL_REAL + Constants.OTHER_PICS_POSTFIX;

            }else{
                image = Constants.IMAGE_BASE_URL_REAL + Constants.SERVICE_IMAGES_POSTFIX;
                //.append("/").append(Constants.FULL_SIZE).append("/").append(Constants.FULL_SIZE)
                //.append(Constants.SERVICE_IMAGES_POSTFIX).toString();
            }
            // Log.d("Full_Screen_Adapter", "IMAGE_MAIN_PATH: " + image);
        }
        //else{
            //images = QuestionProvider.getInstance().getJobImages();
      //  }
    }

    @Override
    public int getCount() {
        if(imagePaths != null){
            return imagePaths.length;
        }
//        else if(images != null){
//            return images.size();
//        }
        else{
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.fullscreen_image_layout, container,
                false);
        TouchImageView imgDisplay = (TouchImageView) viewLayout.findViewById(R.id.imgDisplay);
        Button btnClose = (Button) viewLayout.findViewById(R.id.btnClose);
        if(imagePaths != null){
            String imagePath = image + imagePaths[position] + ";width=" + (screenSize[0]) + ";height=" + (screenSize[1])+";"; //* 2 / 3
            try{
                Picasso.with(activity.getApplicationContext()).load(imagePath).fit().error(R.drawable.reload_small).into(imgDisplay);
            }catch(Exception e){
                e.printStackTrace();
            }

        }
//        else if(images != null){
//            byte[] imageArray = images.get(position).getImageEncoded();
//            if (imageArray != null) {
//                ArmutUtils.setImage(imgDisplay, imageArray, 1);
//                //imgDisplay.setImageBitmap(BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length));
//            }
//        }
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        Bitmap bitmap = BitmapFactory.decodeFile(imagePaths.get(position), options);
//        imgDisplay.setImageBitmap(bitmap);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
                MainActivity.setBackwardsTranslateAnimation(activity);
            }
        });
        container.addView(viewLayout);
        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
