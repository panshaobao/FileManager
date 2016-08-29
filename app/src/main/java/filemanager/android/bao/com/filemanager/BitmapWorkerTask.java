package filemanager.android.bao.com.filemanager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class BitmapWorkerTask extends AsyncTask<String,Bitmap,Bitmap> {
//    private	final WeakReference imageViewReference;
    private	String path;
    private ImageView imageView;
    public	BitmapWorkerTask(ImageView imageView)	{
//        imageViewReference = new WeakReference(imageView);
        this.imageView = imageView;
    }
    @Override
    protected Bitmap doInBackground(String...params)	{
        path = params[0];
        return decodeSampledBitmapFromResource(path,100,100);
    }
    @Override
    protected void onPostExecute(Bitmap	bitmap)	{
//        if	(imageViewReference	!=	null	&&	bitmap	!=	null)	{
//            final ImageView imageView = (ImageView) imageViewReference.get();
//            if (imageView != null) {
//                imageView.setImageBitmap(bitmap);
//            }
//        }

        if (imageView != null){
            imageView.setImageBitmap(bitmap);
        }
    }

    public Bitmap decodeSampledBitmapFromResource(String filePath,
                                                      int reqWidth, int reqHeight)	{
        final BitmapFactory.Options options	= new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }


    public	static	int	calculateInSampleSize(
            BitmapFactory.Options options,int reqWidth,int reqHeight)	{
        final	int	height	= options.outHeight;
        final	int	width = options.outWidth;
        int	inSampleSize = 1;
//        if	(height	>	reqHeight	||	width	>	reqWidth)	{
//            final	int	halfHeight	=	height	/	2;
//            final	int	halfWidth	=	width	/	2;
//            while	((halfHeight/inSampleSize)>reqHeight
//                    &&	(halfWidth/inSampleSize)>reqWidth){
//                inSampleSize *=	2;
//            }
//        }

        int h = (int)Math.ceil(height/reqHeight);
        int w =(int)Math.ceil(width/reqWidth);

        if (h>1 && w>1){
            if (h>w){
                inSampleSize=h;
            }else {
                inSampleSize=w;
            }
        }


        return	inSampleSize;
    }




}
