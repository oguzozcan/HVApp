package com.armut.armuthv.objects;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.armut.armuthv.FullScreenImageActivity;
import com.armut.armuthv.MainActivity;
import com.armut.armuthv.R;
import com.armut.armuthv.utils.ArmutUtils;
import com.armut.armuthv.utils.Constants;
import com.armut.armuthv.utils.TimeUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by oguzemreozcan on 27/06/16.
 */
public class QuestionAnswerView implements View.OnClickListener {

    private TextView questionTv;
    private TextView answerTv;
    private final int pageNumber;
    private final QAOnClickListener callback;
    private final boolean usesLocalImages;
    private final int controlId;
    private LinearLayout layout;
    private final Activity activity;
    private LayoutInflater inflater;
    private String[] photoNames;

    public QuestionAnswerView(Activity activity, QAOnClickListener callback, ViewGroup parent, int pageNumber, int controlId, final boolean usesLocalImages, boolean lastItem) {
        this.activity = activity;
        Log.d("QA", "QUESTION ANSWER VIEW: " + pageNumber + " - controlId: " + controlId);
        this.callback = callback;
        this.pageNumber = pageNumber;
        this.controlId = controlId;
        this.usesLocalImages = usesLocalImages;
        init(activity, parent, lastItem);
    }

    private void init(Context context, ViewGroup parent, boolean lastItem) {
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, activity.getResources().getDisplayMetrics());
            if (controlId != 8000000) {
                layout = (LinearLayout) inflater.inflate(R.layout.question_answer_text_layout, null);
                questionTv = (TextView) layout.findViewById(R.id.questionTv);
                answerTv = (TextView) layout.findViewById(R.id.answerTv);
//                if (!isWhiteColored) {
//                    questionTv.setTextColor(ContextCompat.getColor(context, R.color.blackish));
//                    answerTv.setTextColor(ContextCompat.getColor(context, R.color.blackish));
//                }
            } else {
                layout = (LinearLayout) inflater.inflate(R.layout.job_photo_slider, null);
                questionTv = (TextView) layout.findViewById(R.id.question);
                if (usesLocalImages) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.bottomMargin = margin / 2;
                    params.topMargin = margin / 2;
                    params.gravity = Gravity.CENTER_HORIZONTAL;
                    questionTv.setLayoutParams(params);
                }
                //int fontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, context.getResources().getDisplayMetrics());
                //questionTv.setTextSize(fontSize);
                //LinearLayout sliderLayout = (LinearLayout) view.findViewById(R.id.photoLayout);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);//(LinearLayout.LayoutParams) layout.getLayoutParams();
            params.bottomMargin = lastItem ? 2 * margin : margin / 2;
            layout.setLayoutParams(params);
            layout.setOnClickListener(this);
            parent.addView(layout);
        }
    }

    public void setBackground(int color) {
        if (layout != null)
            layout.setBackgroundColor(color);
    }

    public void setQuestionText(String question) {
        if (questionTv != null)
            questionTv.setText(question);
    }

    public void setAnswerText(String answer) {
        Log.d("QA_VIEW", "ANSWER: " + answer);
        if (answer == null) {
            answer = " - ";
        } else if (answer.equals("")) {
            answer = " - ";
        }
        if (answerTv != null) {
            //9000004 is date so convert to readable form
            if(controlId != 9000004){
                answerTv.setText(answer);
            }else{
                answerTv.setText(TimeUtils.convertDateTimeFormat(TimeUtils.updateDateFormat, TimeUtils.dtfOutWithWeek, answer));
            }

        } else {
            if (answer.equals(" - ")) {
                layout.setVisibility(View.GONE);
            } else {
                int index = 0;
                if (usesLocalImages) {
//                    String[] photoNames = answer.split("\\" + Constants.SEPERATOR);
//                    for(String photo : photoNames){
//                        Log.d("TAG", "Photo Names: " + photo);
//                    }
                    //TODO
                    //ArrayList<ImageInfo> images = QuestionProvider.getInstance().getJobImages();
//                    Log.d("QA_VIEW", "JOB IMAGES: " + images.size());
//                    for (ImageInfo image : images) {
//                        addJobImage(image, null, index);
//                        index++;
//                    }
                } else {
                    photoNames = answer.split("\\" + Constants.VISUAL_SEPERATOR);
                    for (String photo : photoNames) {
                        Log.d("TAG", "Photo Names: " + photo);
                        addJobImage(null, photo.trim(), index);
                        index++;
                    }
                }
            }
        }
    }

    private void addJobImage(final ImageInfo imageInfo, final String url, final int index) {
        View view = inflater.inflate(R.layout.job_image_view, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.jobImage);
        ImageView closeButton = (ImageView) view.findViewById(R.id.deleteJobImageButton);
        closeButton.setVisibility(View.GONE);
        if (imageInfo != null) {
            byte[] imageArray = imageInfo.getImageEncoded();
            if (imageArray != null) {
                //imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length));
                ArmutUtils.setImage(imageView, imageArray, 1);
            }
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (callback != null)
                        callback.onItemClick(pageNumber);
                }
            });
        } else if (url != null && activity != null) {
//            String photoFullUrl = new StringBuilder(Constants.IMAGE_BASE_URL_REAL).append("/").append(Constants.FULL_SIZE).append("/").append(Constants.FULL_SIZE)
//                    .append(Constants.SERVICE_IMAGES_POSTFIX).toString();
            try {
                int[] screenSize = ArmutUtils.getScreenSize(activity);
                String photoFullUrl = Constants.IMAGE_BASE_URL_REAL + Constants.SERVICE_IMAGES_POSTFIX + url + ";width=" + screenSize[0] + ";height=" + screenSize[1] + ";mode=crop;"; // /3*2
                Picasso.with(activity.getApplicationContext()).load(photoFullUrl).fit().into(imageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openPhotoManager(index);
                }
            });
        }
        LinearLayout photoLayoutMain = (LinearLayout) layout.findViewById(R.id.photoLayout);
        if (usesLocalImages) {
            layout.setBackgroundResource(R.drawable.qa_unselected_shape);
        }
        try {
            photoLayoutMain.addView(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    private void setImage(ImageView view, byte[] imageArray){
//        if (imageArray != null) {
//            //view.setImageBitmap(BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length));
//            BitmapFactory.Options options=new BitmapFactory.Options();// Create object of bitmapfactory's option method for further option use
//            options.inPurgeable = true; // inPurgeable is used to free up memory while required
//            Bitmap songImage1 = BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length,options);//Decode image, "thumbnail" is the object of image file
//            Bitmap songImage = Bitmap.createScaledBitmap(songImage1, 100 , 100 , true);// convert decoded bitmap into well scalled Bitmap format.
//            //songImage1.recycle();
//            //songImage1 = null;
//            view.setImageDrawable(songImage);
//            songImage.recycle();
//            //songImage = null;
//        }
//    }

    private void openPhotoManager(int position) {
        if (activity != null && photoNames != null) {
            Intent intent = new Intent(activity, FullScreenImageActivity.class);
            intent.putExtra("POSITION", position);
            intent.putExtra("ARRAY", photoNames);
            intent.putExtra("PROFILE_PICS", false);
            activity.startActivity(intent);
            MainActivity.setTranslateAnimation(activity);
            Log.d("QAView", "OPEN PHOTO MANAGER: " + position);
        }
    }

    public void setAnswerTextColor(int color) {
        if (answerTv != null)
            answerTv.setTextColor(color);
    }

    public void setQuestionTextColor(int color) {
        if (questionTv != null)
            questionTv.setTextColor(color);
    }

    public void setBackgroundColor(int color) {
        if (layout != null) {
            layout.setBackgroundColor(color);
        }
    }

    public void setAnswerTextSize(float size) {
        if (answerTv != null)
            answerTv.setTextSize(size);
    }

    public void setQuestionTextSize(float size) {
        if (questionTv != null)
            questionTv.setTextSize(size);
    }

    @Override
    public void onClick(View v) {
        if (callback != null)
            callback.onItemClick(pageNumber);
    }

    interface QAOnClickListener {
        void onItemClick(int pageNumber);
    }
}