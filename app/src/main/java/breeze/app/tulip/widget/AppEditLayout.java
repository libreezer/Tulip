package breeze.app.tulip.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import breeze.app.tulip.R;

public class AppEditLayout extends LinearLayout {

    static {
        System.loadLibrary("app");
    }

    public native void setData(String mn);

    public AppEditLayout(Context context) {
        super(context);
    }

    public AppEditLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public AppEditLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private EditText editText;
    private String methodName,parentClass;
    private TextView tag;

    public String getParentClass() {
        return parentClass;
    }

    public void setParentClass(String parentClass) {
        this.parentClass = parentClass;
    }

    private void init(Context context, AttributeSet attributeSet){
        View inflate = LayoutInflater.from(context).inflate(R.layout.view_edit_with_tag, this, true);
        tag = inflate.findViewById(R.id.appedit_tag);
        editText = inflate.findViewById(R.id.appedit_editText);
        @SuppressLint("Recycle")
        TypedArray typedArray = context.obtainStyledAttributes(
                attributeSet, R.styleable.AppEditLayout);
        tag.setTextColor(typedArray.getColor(
                R.styleable.AppEditLayout_android_textColor, Color.BLACK));
        tag.setText(typedArray.getString(R.styleable.AppEditLayout_inputTag));
        methodName = typedArray.getString(R.styleable.AppEditLayout_methodName);
        parentClass = typedArray.getString(R.styleable.AppEditLayout_parentClass);
        setEnabled(typedArray.getBoolean(R.styleable.AppEditLayout_android_enabled,true));
        setData(methodName);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled){
            tag.setTextColor(Color.parseColor("#F07000"));
        }else {
            tag.setTextColor(Color.parseColor("#cccccc"));
        }
        tag.setEnabled(enabled);
        editText.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    public String getMethodName(){
        return this.methodName;
    }

    public String  getText(){
        return editText.getText().toString();
    }

    public void setEditText(String content){
        editText.setText(content);
    }

    public void setTag(String content){
        tag.setText(content);
    }

    public void setMethodName(String content){
        methodName = content;
    }
}
