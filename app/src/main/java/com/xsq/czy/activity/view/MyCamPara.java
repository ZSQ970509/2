package com.xsq.czy.activity.view;

import android.hardware.Camera.Size;
import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MyCamPara {  
    private static final String tag = "yan";  
    private CameraSizeComparator sizeComparator = new CameraSizeComparator();  
    private static MyCamPara myCamPara = null;  
    private MyCamPara(){  
          
    }  
    public static MyCamPara getInstance(){  
        if(myCamPara == null){  
            myCamPara = new MyCamPara();  
            return myCamPara;  
        }  
        else{  
            return myCamPara;  
        }  
    }  
      
    public  Size getPreviewSize(List<Size> list, int th){
        Collections.sort(list, sizeComparator);

        int i = 0;
        for(Size s:list){
        	Log.i(tag, "�����ߴ�:w = " + s.width + "h = " + s.height);

            if((s.width < th) && equalRate(s, 1.33f)){
                Log.i(tag, "��������Ԥ���ߴ�:w = " + s.width + "h = " + s.height);
                break;
            }
            i++;
        }

        return list.get(i);
    }
    public Size getPictureSize(List<Size> list, int th){
        Collections.sort(list, sizeComparator);

        int i = 0;
        for(Size s:list){
        	Log.i(tag, "�����ߴ�:w = " + s.width + "h = " + s.height);

            if((s.width < th) && equalRate(s, 1.33f)){
                Log.i(tag, "��������ͼƬ�ߴ�:w = " + s.width + "h = " + s.height);
                break;
            }
            i++;
        }
        return list.get(i);
    }


    /**
     * ��ȡͬʱ����Ԥ�������ճߴ��Camera.Size
     * @param Frelist
     * @param Piclist
     * @param th
     * @return
     */
	public Size getPretureSizeaAndPicviewSize(List<Size> Frelist, List<Size> Piclist, int th) {
		 Collections.sort(Frelist, sizeComparator);
		Collections.sort(Piclist, sizeComparator);

		int i = 0;
		for (Size FreS : Frelist) {
			Log.i(tag, "Ԥ�������ߴ�:w = " + FreS.width + "h = " + FreS.height);

			if ((FreS.width < th) && equalRate(FreS, 1.3333f)) {
				Log.i(tag, "��ѯ������Ԥ��ͼƬ�ߴ�:w = " + FreS.width + "h = " + FreS.height);
				for (Size PicS : Piclist) {
					Log.i(tag, "���ձ����ߴ�:w = " + PicS.width + "h = " + PicS.height);
					if ((PicS.width < th) && equalRate(PicS, 1.3333f)) {

						//�ҵ�Ԥ��������һ���ĳ���
						if (FreS.width == PicS.width && FreS.height == PicS.height) {
							Log.i(tag, "��������ͼƬ�ߴ�:w = " + FreS.width + "h = " + FreS.height);
							return Frelist.get(i);
						}
					}
				}
			}
			i++;
		}
		Log.i(tag, "---------------------");

		return getPictureSize(Piclist, th);
	}



    public boolean equalRate(Size s, float rate){
        float r = (float)(s.width)/(float)(s.height);

        if(Math.abs(r - rate) <= 0.00334)
        {
        	Log.d(tag, "С��0.00334:"+Math.abs(r - rate));
            return true;
        }
        else{
        	Log.d(tag, "����0.00334:"+Math.abs(r - rate));
            return false;
        }
    }

    public  class CameraSizeComparator implements Comparator<Size>{
        //����������  
        public int compare(Size lhs, Size rhs) {  
            // TODO Auto-generated method stub  
            if(lhs.width == rhs.width){  
            return 0;  
            }  
            else if(lhs.width > rhs.width){  
                return -1;  
            }  
            else{  
                return 1;  
            }  
        }  
          
    }  
}  