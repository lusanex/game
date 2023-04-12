package xyz.erichg

import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

class MainActivity : AppCompatActivity() {


    private var density : Int = -1
    private lateinit var gameView : ImageView
    private lateinit var blankBitmap: Bitmap
    private lateinit var canvas: Canvas
    private lateinit var paint: Paint
    private var numberHorizontalPixels: Int = -1
    private var numberVerticalPixels: Int = -1
    private var numberHorizontalDPI: Int = -1
    private  var numberVerticalDPI: Int = -1
    private var blockSizeInPixels: Int = -1
    private var blockSizeInDP: Int = -1
    private var gridWidthInPixels: Int = -1
    private var gridHeightInPixels: Int = -1
    private var gridWidthInDP : Int = -1
    private var gridHeightInDP: Int = -1
    private var horizontalTouchedInPixels: Float = -101F
    private var verticalTouchedInPixels: Float = -101F
    private var horizontalTouchedDP: Float = -1f
    private var verticalTouchedDP: Float = -1f
    private var subHorizontalPositionInPixels: Int = -1
    private var subVerticalPositionInPixels: Int = -1
    private var subHorizontalPositionDP: Int = -1
    private var subVerticalPositionDP: Int = -1
    private var hit: Boolean = false
    private var shotsTaken: Int = -1
    private var distanceFromSubInPixels: Int = -1
    private var insets: Rect = Rect()
    private var debugging: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        WindowCompat.setDecorFitsSystemWindows(window,false)



        val sizeInPixels = Point(-1,-1)
        getWindowSize(sizeInPixels)
        numberHorizontalPixels = sizeInPixels.x
        numberVerticalPixels = sizeInPixels.y
        density = resources.displayMetrics.density.toInt()
        numberHorizontalDPI = numberHorizontalPixels /density
        numberVerticalDPI = numberVerticalPixels / density
        val numBlockHorizontal = 10
        val numBlockVertical = 15
        val blockWidth = numberHorizontalDPI / numBlockHorizontal
        val blockHeight = numberVerticalDPI / numBlockVertical

        blockSizeInDP = min(blockWidth,blockHeight)
        gridWidthInDP = blockWidth
        gridHeightInDP = blockHeight
        blockSizeInPixels = (blockSizeInDP * density + 0.5f).toInt()


        blankBitmap =
            Bitmap.createBitmap(numberHorizontalPixels,
            numberVerticalPixels,
            Bitmap.Config.ARGB_8888)
        canvas = Canvas(blankBitmap)
        gameView = ImageView(this)
        paint = Paint()
        setContentView(gameView)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val windowInsetsController = gameView.windowInsetsController
            // your code for Android 12 and above
            windowInsetsController?.hide(WindowInsets.Type.systemBars())
            windowInsetsController?.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_DEFAULT
            windowInsetsController?.hide(WindowInsets.Type.navigationBars())

        } else {
            // your code for older Android versions
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }



        if (debugging)
        {
            Log.d("Debugging","In onCreate")
            Log.d("In Create","numberHorizontalDP $numberHorizontalDPI numberVerticalDP: $numberVerticalDPI")
            Log.d("In onCreate","blockSizeInDP: $blockSizeInDP")
            Log.d("In onCreate","blockWidth: $blockWidth blockHeight: $blockHeight")


        }
        newGame()
        draw()
    }



    private fun draw() {

        gameView.setImageBitmap(blankBitmap)
        canvas.drawColor(Color.argb(255,255,255,255))

        paint.color = Color.argb(255,0,0,0)

        for ( i in 0 until gridHeightInDP)
        {
            canvas.drawLine(0f,
                blockSizeInPixels * i.toFloat(),
                numberHorizontalPixels.toFloat(),
                blockSizeInPixels * i.toFloat(),
                paint)

        }

        for( i in 0 until gridWidthInDP)
        {
            canvas.drawLine(blockSizeInPixels * i.toFloat(),
                0f,
                blockSizeInPixels * i.toFloat(),
                numberVerticalPixels.toFloat(),
                paint)
        }


        //score
        paint.textSize = blockSizeInPixels.toFloat() * .5f
        paint.color = Color.argb(255,0,0,255)
        canvas.drawText(
            "Shots Taken: $shotsTaken Distance: $distanceFromSubInPixels",
            blockSizeInPixels.toFloat(),blockSizeInPixels * 2f,
            paint
        )

        if (debugging)
        {

            printDebuggingText()
        }

    }

    private fun  drawScore()
    {
        paint.textSize = blockSizeInPixels.toFloat()


    }

    private fun getWindowSize(point: Point) {


        val displayMetrics = DisplayMetrics()
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            val ins =
                windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars())
            insets.left = ins.left
            insets.right = ins.right
            insets.top = ins.top
            insets.bottom = ins.bottom

            displayMetrics.widthPixels = windowMetrics.bounds.width() - ins.left - ins.right
            displayMetrics.heightPixels = windowMetrics.bounds.height() - ins.top - ins.bottom


        }
        else
        {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(displayMetrics)

        }

        point.x = displayMetrics.widthPixels
        point.y = displayMetrics.heightPixels

        if (debugging) {

            Log.d("Debugging", "In getWindowSize")

            Log.d("In getWindowSize","insets.left: ${insets.left} insets.right: ${insets.right} " +
                    "insets.top: ${insets.top} insets.bottom: ${insets.bottom}")
            Log.d("In getWindowSize", "widthPixels: ${point.x}")
            Log.d("In getWindowSize", "heightPixels: ${point.y}")
        }
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {


        if( event?.action?.and(MotionEvent.ACTION_MASK)
                == MotionEvent.ACTION_UP)
        {
            takeShot(event.x,event.y)

        }
        if(debugging)
        {
            Log.d("Debugging","In onTouchEvent")
            Log.d("event.x ${event?.x}","event.y ${event?.y}")
        }
        return true
    }

    private fun takeShot(xPixels: Float, yPixels: Float) {

        shotsTaken++

        val xDP = xPixels / density
        val yDP = yPixels / density
        horizontalTouchedDP = (xDP.toInt() / blockSizeInDP).toFloat()
        verticalTouchedDP = (yDP.toInt()/ blockSizeInDP).toFloat()

        horizontalTouchedInPixels = (horizontalTouchedDP * blockSizeInDP * density + 0.5).toFloat()
        verticalTouchedInPixels = ( verticalTouchedDP * blockSizeInDP * density + 0.5).toFloat()


        draw()

        if(debugging)
        {
            Log.d("Debugging","In takeShot")
            Log.d("In takeShot","horizontalTouchedDP $horizontalTouchedDP verticalTouchedDP $verticalTouchedDP")
            Log.d("In takeShot","horizontalTouchedPixels $horizontalTouchedInPixels" +
            "verticalTouchedPixels $verticalTouchedInPixels")
        }


    }

    private fun boom() {
        gameView.setImageBitmap(blankBitmap)
        canvas.drawColor ( Color.argb(254,255,0,0))
        paint.color = Color.argb(254,255,255,255)

        newGame()
    }
    // This code prints the debugging text
    // to the device's screen
    private fun printDebuggingText() {

    }
    private fun newGame() {

        subHorizontalPositionDP = Random.nextInt(numberHorizontalDPI/blockSizeInDP)
        subVerticalPositionDP = Random.nextInt(numberVerticalDPI/blockSizeInDP)
        shotsTaken = 0

        if(debugging)
        {

            Log.d("Debugging","In newGame")
            Log.d("In newGame","subHorizontalPositionDP $subHorizontalPositionDP subVerticalPositionDP: $subVerticalPositionDP")
        }

    }


}