package com.xsq.common.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xsq.common.R;
import com.xsq.common.util.ColorsUtil;


public class SqureButton extends RelativeLayout {

	private View self = null;
	
	private onClickCallback cb = null;
	
	public SqureButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttrAndView(context, attrs);
		initListener();
	}
	
	@SuppressLint("NewApi")
	private void initAttrAndView(Context context, AttributeSet attrs){
		inflate(context, R.layout.component_squrebutton, this);
		self = getChildAt(0);
		if(attrs!=null){
			TypedArray attr = context.obtainStyledAttributes(attrs,R.styleable.SqureButton);
			
			if(attr.hasValue(R.styleable.SqureButton_text)){
				String label = attr.getString(R.styleable.SqureButton_text);
				setLabel(label);
			}
			if(attr.hasValue(R.styleable.SqureButton_textColor)){
				int color = attr.getColor(R.styleable.SqureButton_textColor, 0xffffffff);
				setLabelColor(color);
			}
			if(attr.hasValue(R.styleable.SqureButton_textSize)){
				float size = attr.getDimension(R.styleable.SqureButton_textSize, 20f);
				setLabelSize(size);
			}
			if(attr.hasValue(R.styleable.SqureButton_backgourd)){
				Drawable bgDraw = attr.getDrawable(R.styleable.SqureButton_backgourd);
				self.setBackgroundDrawable(bgDraw);
			}

			if(attr.hasValue(R.styleable.SqureButton_ico)){
				Drawable icoDraw = attr.getDrawable(R.styleable.SqureButton_ico);
				((ImageView)self.findViewById(R.id.comp_sqr_ico)).setBackgroundDrawable(icoDraw);
				
			}
			attr.recycle();
		}

	}
	
	private void initListener(){
		self.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(cb!=null)
					cb.onClick(arg0);
			}
		});
	}
	
	public void setLabel(String label){
		if(label!=null)
			((TextView)self.findViewById(R.id.comp_sqr_label)).setText(label);
	}
	
	public String getLabel(){
		return ((TextView)self.findViewById(R.id.comp_sqr_label)).getText().toString();
	}
	
	public void setLabelColor(int color){
		((TextView)self.findViewById(R.id.comp_sqr_label)).setTextColor(color);
	}
	
	public void setLabelSize(float size){
		((TextView)self.findViewById(R.id.comp_sqr_label)).setTextSize(size);
	}
	
	public void setOnClickCallback(onClickCallback cb){
		this.cb = cb;
	}
	
	public static interface onClickCallback{
		public void onClick(View v);
	}

}
