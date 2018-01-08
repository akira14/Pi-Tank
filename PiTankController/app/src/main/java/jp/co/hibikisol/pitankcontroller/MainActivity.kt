package jp.co.hibikisol.pitankcontroller

import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.webkit.WebViewClient
import android.widget.SeekBar
//import kotlinx.android.synthetic.main.activity_main.seekBar
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.R.attr.x
import android.graphics.*
import android.view.Display
import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.ConnectException
import java.net.Socket


class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    private var isTimerStarted = false
//    private var handler = Handler()
    private var tankIP: String? = null
    private var r = object : Runnable {
        override fun run() {
            Log.d("run", "-----------------------")
            var socket: Socket? = null
            var out: OutputStream? = null
            var bw: BufferedWriter? = null
            try {

                socket = Socket(tankIP, 49002)
                out = socket.getOutputStream()
                bw = BufferedWriter(OutputStreamWriter(out, "UTF-8"))

                while (true) {
                    Log.d("run", "******* L: " + mySeekBarL.progress + " R: " + mySeekBarR.progress)
                    bw.write("L" + mySeekBarL.progress + " R" + mySeekBarR.progress);
                    bw.newLine()
                    bw.flush()
                    Thread.sleep(1000)
                }
            } catch (e: Exception) {
                Log.d("run", e.message)
                bw?.close()
                out?.close()
                socket?.close()
            }

            //handler.postDelayed(this, 1000)
        }
    }
    private var thread = Thread(r)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 設定情報の取得
        var intent = getIntent()
        tankIP = intent.getStringExtra("TANK-IP")

        // WebViewの設定 -> ライブカメラ表示
        webView.setWebViewClient(WebViewClient())
        webView.loadUrl("http://" + tankIP + ":" + 8080)
        webView.setOnTouchListener { view, motionEvent -> true }

//        button2.setOnClickListener {
//            Log.d("111111","aaaaaaaaa")
//            //imageView2.visibility = View.INVISIBLE
//
//            HttpGetTask().execute()
//        }

        // SeekBarの初期化
        drawSeekScale(mySeekBarL)
        drawSeekScale(mySeekBarR)
        mySeekBarL.setOnSeekBarChangeListener(this)
        mySeekBarR.setOnSeekBarChangeListener(this)
    }

    private fun drawSeekScale(seekBar: SeekBar) {

        val display = windowManager.defaultDisplay
        val displaysize = Point()
        display.getSize(displaysize)
        val width = displaysize.x
        //val width = mySeekBarL.x
        val height = displaysize.y

        val bitmap = Bitmap.createBitmap(height, 100, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint()
        paint.setColor(0xaacccccc.toInt())
        paint.setStyle(Paint.Style.STROKE)
        paint.setStrokeWidth(5f)

        val paint2 = Paint()
        paint2.setColor(0xaaffff00.toInt())
        paint2.setStyle(Paint.Style.STROKE)
        paint2.setStrokeWidth(10f)

        val paint3 = Paint()
        paint3.setAntiAlias(true);
        paint3.setColor(0x55000000.toInt())
        paint3.setStyle(Paint.Style.FILL)
        canvas.drawRect(0f, 0f, height - 1f, 99f, paint3)

        var point = 0
        var seekbarpoints = height / 10
        for (i in 0 until seekbarpoints) {
            if (i == 4) {
                point = point + seekbarpoints
                canvas.drawLine(point - 0f, 99f, point + 0f, 0f, paint2)
            } else if (i % 2 == 0) {
                point = point + seekbarpoints
                canvas.drawLine(point - 0f, 99f, point -0f, 0f, paint)
            } else {
                point = point + seekbarpoints
                canvas.drawLine(point - 0f, 99f, point-0f, 0f, paint)
            }
        }

        val d = BitmapDrawable(resources, bitmap)

        seekBar.setProgressDrawable(d)
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int,
                                   fromUser: Boolean) {

        if ((45 <= mySeekBarL.progress && mySeekBarL.progress <= 55)
                && (45 <= mySeekBarR.progress && mySeekBarR.progress <= 55)) {
            if (isTimerStarted) {
                Log.d("Stop","!!!!!!!!!!!!!")
                isTimerStarted = false
                //handler.removeCallbacks(r);
                thread.interrupt()
            }
            return
        }

        // LRどちらかがONであれば通信開始
        if (!isTimerStarted) {
            Log.d("Sart","---------------")
            isTimerStarted = true
            //handler.post(r)
            thread = Thread(r)
            thread.start()
        }
    }

    /*
    inner class HttpGetTask : AsyncTask<Void, Void, Bitmap>() {

        override fun onPreExecute() {
        }

        override fun doInBackground(vararg params: Void): Bitmap? {
            var bitmap: Bitmap
            try {
                val url = URL("https://upload.wikimedia.org/wikipedia/commons/6/6b/Phalaenopsis_JPEG.jpg")
                val connection = url.openConnection() as HttpURLConnection
                connection.setDoInput(true)
                connection.connect()
                val input = connection.getInputStream()
                bitmap = BitmapFactory.decodeStream(input)
                return bitmap

            } catch (e: IOException) {
            }
            return null
        }

        override fun onProgressUpdate(vararg values: Void?) {
        }

        override fun onPostExecute(result: Bitmap?) {
            imageView2.setImageBitmap(result)
            Log.d("222","bbbbbbbb")
        }
    }
    */
}
