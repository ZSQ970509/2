package com.xsq.common.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {

	private View remainView = null;

	protected boolean isInstanceAgain(){
		return false;
	}

	/** 获得布局layoutId
	 * @return
	 */
	protected abstract int getLayoutId(); 
	
	
	/** 返回之前保存的view视图
	 * @return
	 */
	protected final View getRemainView(){
		return remainView;
	}
	
	/** fragment初始化前回调函数
	 * @param savedInstanceState
	 */
	protected void onFragmentPreInit(Bundle savedInstanceState) {
		
	}
	
	/** fragment初始化layout进行中时回调函数
	 * @param savedInstanceState
	 * @param v
	 */
	protected void onFragmentInit(Bundle savedInstanceState,View v){
		
	};
	
	/** activity初始化完成回调函数
	 * @param savedInstanceState
	 */
	protected void onFragmentIntact(Bundle savedInstanceState){
		
	};
	
	public View findViewById(int id){
		if(getView()!=null){
			return getView().findViewById(id);
		}
		return null;
	}
	
	@Override
	public final void onCreate(Bundle savedInstanceState) {
		onFragmentPreInit(savedInstanceState);
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public final View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = getRemainView();
		if(v == null){
			v = inflater.inflate(getLayoutId(), container, false);
			onFragmentInit(savedInstanceState,v);
			if(v == null)
				return super.onCreateView(inflater, container, savedInstanceState);
		}

		if(v != null && isInstanceAgain() == false){
			remainView = v;
		}
		return v;
	}


	@Override
	public final void onActivityCreated(@Nullable Bundle savedInstanceState) {
		onFragmentIntact(savedInstanceState);
		super.onActivityCreated(savedInstanceState);
	}

	
	
}
