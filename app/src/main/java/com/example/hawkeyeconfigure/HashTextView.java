package com.example.hawkeyeconfigure;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;


import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.constraintlayout.widget.ConstraintLayout;

public class HashTextView extends ConstraintLayout {
    String[] SPG =null;
    String[] SPV =null;
    int ImeOption=-1;
    String labelname = "NULL";
    String value;
    int type;
    View view;
    TextInputLayout textInputLayout;
    TextInputEditText et;
    int AllowBlank=0;
    int Valid = 0;
    boolean errset=false;
    Animation shake;

    ProgressBar progressBar;
    Context context;

    public boolean isLocked = false;
    ImageButton camimage;
    ImageView flowview;
    int defaultCameraSide;
    ImageView bg;
    ConstraintLayout mainview;

    public boolean isLocked() {
        return isLocked;
    }

    public HashTextView(Context context) {
        super(context);
    }

    public String[] getSPG() {
        return SPG;
    }


    public int getType() {
        return type;
    }

    public String[] getSPV() {
        return SPV;
    }

    public void setSPG(String spg) {
        if(spg.trim().length()>0)
        {
            this.SPG = spg.trim().split(",");
        }
        else
        {
            this.SPG=null;
        }

    }

    public void setSPV(String spv) {
        if(spv.trim().length()>0)
        {
            this.SPV = spv.trim().split(",");
        }
        else
        {
            this.SPV=null;
        }
    }

    public int getAllowBlank() {
        return AllowBlank;
    }

    public void setAllowBlank(int allowBlank) {
        AllowBlank = allowBlank;
    }

    public HashTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //setOrientation(LinearLayout.VERTICAL);
        //setGravity(Gravity.CENTER_VERTICAL);

        Log.w("HashTextView","initing");


        this.context=context;

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.HashTextView,
                0, 0);
        initAttributes(a);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(type==9)
        {
            view = inflater.inflate(R.layout.hash_text_view_image, this, true);
        }
        else {
            view = inflater.inflate(R.layout.hash_text_view, this, true);
        }

        textInputLayout = view.findViewById(R.id.usernamelayout);
        et=view.findViewById(R.id.hashtextview);

        mainview=view.findViewById(R.id.view);
        flowview = view.findViewById(R.id.flowview);
        camimage=view.findViewById(R.id.camimg);
        bg = view.findViewById(R.id.bg);
        //et.setText("");

        a.recycle();
        setLabelName();
        setType();
        progressBar = view.findViewById(R.id.progressBar);
        if(type!=9)
        {
            progressBar.setVisibility(View.GONE);
        }

//        if(hasValidation()&&AllowBlank==0)
//        {
//            Valid=0;
//        }
//        if(AllowBlank==1)
//        {
//            Valid=1;
//        }
//
//        else {
//            Valid=0;
//        }
        Reload();

        if(ImeOption==0)
        {
            et.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        }
        else if(ImeOption==1)
        {
            et.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }
        else if(ImeOption==2)
        {
            //et.setImeOptions(EditorInfo.IME_ACTION_DONE|EditorInfo.IME_ACTION_NEXT);
        }

        shake = AnimationUtils.loadAnimation(context, R.anim.shake);


        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                value=charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    public void Hide()
    { mainview.setVisibility(GONE); }

    public void Unhide()
    { mainview.setVisibility(VISIBLE); }

    public View getFlowview() {
        if(type==9)
        {
            return flowview;
        }
        else
        {
            return mainview;
        }
    }

    public ImageButton getCamimage() {
        return camimage;
    }

    public void Reload()
    {
        removeError();
        if(hasValidation()&&AllowBlank==0)
        {
            Valid=0;
        }
        if(AllowBlank==1)
        {
            Valid=1;
        }

        else {
            Valid=0;
        }
        value="";
        et.setText("");
        isLocked=false;

        if(type==9)
        {
            camimage.setImageResource(R.drawable.camera_img);
        }

        setType();
    }

    public void setLoading()
    {
        progressBar.setVisibility(VISIBLE);
        if(!(type==0||type==7||type==5))
        {
            et.setEnabled(false);
        }

    }

    public void removeLoading()
    {
        progressBar.setVisibility(GONE);
        if(!(type==0||type==7||type==5))
        {
            et.setEnabled(true);
        }
    }

    public void setValidity(int valid)
    {
        this.Valid=valid;
    }

    public boolean hasGetValue()
    {
        if(SPG==null)
        {
            return false;
        }
        if(SPG.length>0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean hasValidation()
    {
        if(SPV==null)
        { return false; }
        if(SPV.length>0)
        { return true; }
        else
        { return false; }
    }


    public String getLabelID()
    {
        if (view.getId() == View.NO_ID) return "no-id";
        else
        {
            String id =view.getResources().getResourceName(view.getId());
            int i = id.indexOf('/');
            id=id.substring(i+1);
            return id;

        }

    }
    public void SetBitmap(final Bitmap bitmap)
    {
        camimage.post(new Runnable() {
            @Override
            public void run() {
                Bitmap b = resizeBitmap(camimage, bitmap);
                final Bitmap cropped = getCroppedBitmap(b);
                camimage.setImageBitmap(cropped);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        value=encodeBitmap(bitmap);
                        Log.w("bitmap","bitmap length = "+String.valueOf(value.length()));
                    }
                }).start();
                removeError();
                setValidity(1);
            }
        });
    }

    Bitmap resizeBitmap(ImageButton imageView, Bitmap bitMap)
    {
        int currentBitmapWidth = bitMap.getWidth();
        int currentBitmapHeight = bitMap.getHeight();

        int ivWidth = imageView.getWidth();
        int ivHeight = imageView.getHeight();
        int newWidth = ivWidth;

        int newHeight = (int) Math.floor((double) currentBitmapHeight *( (double)newWidth / (double) currentBitmapWidth));

        Bitmap newbitMap = Bitmap.createScaledBitmap(bitMap, newWidth, newHeight, true);

        return newbitMap;
    }


    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    public String encodeBitmap(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        //Log.w("Image",encoded);
        return encoded;
    }

    public void LockField()
    {
        et.setEnabled(false);
        isLocked=true;
        et.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.lock_24, 0);

    }



    public boolean isValid()
    {

        if(isLocked) {
            Log.w("isValid",labelname+" valid cuz locked");
            return true;
        }
        else if(type==9&&value.length()==0)
        {
            Log.w("isValid",labelname+" invalid cuz noimage");
            return false;
        }
//        else if(Valid==1)
//        {
//            return true;
//        }
        else if(AllowBlank==0&&et.getText().length()==0&&value.length()==0&&type!=9)
        {
            Log.w("isValid",labelname+" invalid cuz empty");
            setError("Empty Field");
            value=et.getText().toString();
            return false;
        }
        else if(Valid==0&&hasValidation())
        {
            Log.w("isValid",labelname+" invalid cuz validation not done");
            return false;
        }
        else {
            Log.w("isValid",labelname+" valid cuz else case with value-"+value);
            return true;
        }
    }

    public int getIntID()
    {
        return view.getId();
    }

    public int getLabelType()
    {
        return type;
    }

    public String getLabelName()
    {
        return labelname;
    }

    public void setError(String error)
    {
        textInputLayout.setError(error);
        setValidity(0);
        errset=true;
        mainview.startAnimation(shake);
        //cardView.startAnimation(shake);
        et.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error_24, 0);
    }

    public void removeError()
    {
        textInputLayout.setErrorEnabled(false);
        setValidity(1);
        errset=false;
        //setType();
        if(!isLocked)
        {
            setType();
        }
        else
        {
            isLocked=true;
            et.setEnabled(false);
            et.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.lock_24, 0);
        }
    }


    public void setSubmitError(String msg)
    {
        if(!errset)
        {
            textInputLayout.setError(msg);
        }
        mainview.startAnimation(shake);
        //cardView.startAnimation(shake);
    }


    Bitmap getBitmap(String value)
    {
        byte[] decodedBytes = Base64.decode(value, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
    public TextInputEditText getTextInputEditText()
    {
        return et;
    }


    public TextInputLayout getEtLayout()
    {
        return textInputLayout;
    }
    public void setText(String text,Boolean ByAdmin)
    {
        if(ByAdmin)
        {
            Valid=1;
            removeError();
        }
        else
        {
           if(hasValidation())
           {
               Valid=0;
           }
        }
        Log.w("hashtextview",text);

        if(type==9)
        {
            if(text!=null&&text.length()!=0) {
                Bitmap bitmap = getBitmap(text);
                SetBitmap(bitmap);
            }
        }
        else {
            if (text.toLowerCase().equals("<#LOCK>".toLowerCase())) {
                Log.w("hashtextview","equals");
                et.setEnabled(false);
                isLocked=true;
                et.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.lock_24, 0);
            } else if (text.toLowerCase().equals("<#UNLOCK>".toLowerCase())) {
                et.setEnabled(true);
                isLocked=false;
                setType();
            } else if (text.toLowerCase().contains("<#UNLOCK>".toLowerCase())) {
                text = text.replaceAll("<#UNLOCK>", "");
                et.setText(text.trim());
                value=text.trim();
                isLocked=false;
                et.setEnabled(true);
                setType();
            } else if (text.toLowerCase().contains("<#LOCK>".toLowerCase())) {
                Log.w("hashtextview","contains");
                text = text.replaceAll("<#LOCK>", "");
                et.setEnabled(false);
                isLocked=true;
                et.setText(text.trim());
                value=text.trim();
                et.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.lock_24, 0);
            } else {
                et.setText(text.trim());
                value=text.trim();
            }
        }
    }
    public String getValue()
    {
//        if(type==9)
//        {
//            return value;
//        }
//        return et.getText().toString();
        if(type==3)
        {
            DateFormat originalFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
            DateFormat targetFormat = new SimpleDateFormat("MM.dd.yyyy");
            Date date = null;
            try {
                date = originalFormat.parse(value);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String formattedDate = targetFormat.format(date);
            return formattedDate;
        }
        else
        {
            return value;
        }

    }
    public void setValue(String value)
    {
        if(value.toLowerCase().contains("<#LOCK>".toLowerCase()))
        {
            value=value.replaceAll("<#LOCK>","");
        }
        else if(value.toLowerCase().contains("<#UNLOCK>".toLowerCase()))
        {
            value=value.replaceAll("<#UNLOCK>","");
        }

        this.value=value.trim(); }

    private void setType()
    {

        switch (type)
        {
            case 0:et.setInputType(InputType.TYPE_CLASS_TEXT);et.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);//
                // camimage.setVisibility(GONE);
                break;

            case 1:et.setEnabled(false);isLocked=true;et.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.lock_24, 0);//camimage.setVisibility(GONE);
                 break;

            case 2:et.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.clock_24, 0);//camimage.setVisibility(GONE);
                et.setFocusableInTouchMode(false);break;

            case 3:et.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.calendar_24, 0);//camimage.setVisibility(GONE);
                et.setFocusableInTouchMode(false);break;


            case 4:et.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_qr_code, 0);//camimage.setVisibility(GONE);
                et.setFocusableInTouchMode(false);break;

            case 5:et.setInputType(InputType.TYPE_CLASS_NUMBER);et.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);//camimage.setVisibility(GONE)
                 break;


            case 6:et.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.list_24_vector, 0); et.setFocusableInTouchMode(false);//camimage.setVisibility(GONE);
                 break;

            case 7:et.setInputType(InputType.TYPE_CLASS_NUMBER  | InputType.TYPE_NUMBER_FLAG_DECIMAL);et.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                //camimage.setVisibility(GONE);
                break;




            case 9: et.setVisibility(GONE);//bg.setVisibility(GONE);
                 camimage.setVisibility(VISIBLE);
                 break;

            case 10:et.setEnabled(false);isLocked=true;et.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.lock_24, 0);
            //camimage.setVisibility(GONE);
            break;
        }
    }


    private void setLabelName()
    {

        textInputLayout.setHint(labelname);
    }



    public HashTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public int getDefaultCameraSide() {
        return defaultCameraSide;
    }

    public void setDefaultCameraSide(int defaultCameraSide) {
        this.defaultCameraSide = defaultCameraSide;
    }

    private void initAttributes(TypedArray a)
    {
        try {
             SPG = a.getString(R.styleable.HashTextView_SPG).split(",");
        }catch (Exception e){}
        try {
            SPV = a.getString(R.styleable.HashTextView_SPV).split(",");
        }catch (Exception e){}
        try {
            type = a.getInteger(R.styleable.HashTextView_HashType,0);
        }catch (Exception e){}
        try {
            labelname = a.getString(R.styleable.HashTextView_LabelName);
        }catch (Exception e){}
        try {
            AllowBlank = a.getInteger(R.styleable.HashTextView_AllowBlank,0);
        }catch (Exception e){}
        try {
            ImeOption = a.getInteger(R.styleable.HashTextView_ImeOption,0);
        }catch (Exception e){}

        try {
            defaultCameraSide = a.getInteger(R.styleable.HashTextView_CameraSide,0);
        }catch (Exception e){}


    }


}
