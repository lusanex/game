package xyz.erichg

import android.content.Context
import android.content.res.Configuration
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
import androidx.core.view.ViewCompat.getWindowInsetsController
import androidx.core.view.WindowCompat.setDecorFitsSystemWindows
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

class MainActivity : AppCompatActivity() {



    private var density : Float = 0f
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
    private var distanceFromSubInDP: Int = -1
    private var insets: Rect = Rect()
    private var debugging: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setDecorFitsSystemWindows(window,false)



        val sizeInPixels = Point(-1,-1)
        getWindowSize(sizeInPixels)
        numberHorizontalPixels = sizeInPixels.x
        numberVerticalPixels = sizeInPixels.y
        density = resources.displayMetrics.density
        numberHorizontalDPI = ((numberHorizontalPixels /density) + 0.5 ).toInt()
        numberVerticalDPI = ((numberVerticalPixels / density) + 0.5).toInt()
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
        gameView.layoutParams = ViewGroup.LayoutParams(
    ViewGroup.LayoutParams.MATCH_PARENT,
    ViewGroup.LayoutParams.MATCH_PARENT
)

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
            val controller = getWindowInsetsController(gameView)

// Set the behavior to show transient bars by swipe
            controller?.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

// Hide the navigation bar
            controller?.hide(WindowInsetsCompat.Type.navigationBars())

            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
            )
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

        //draw shot
        drawShot()

        //score
        drawScore()

        if (debugging)
        {

            printDebuggingText()
        }

    }

    private fun drawShot()
    {
        val x: Float = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // device is in portrait mode
            horizontalTouchedDP

        } else {
        // device is in landscape mode
            horizontalTouchedDP
        }
        canvas.drawRect(
            x * blockSizeInPixels,
            verticalTouchedDP * blockSizeInPixels,
            (x * blockSizeInPixels) + blockSizeInPixels,
            (verticalTouchedDP * blockSizeInPixels) + blockSizeInPixels,
            paint
        )

    }
    private fun  drawScore()
    {
        val score = "Shots Taken: $shotsTaken Distance: ${distanceFromSubInDP.coerceAtLeast(0)}"
        paint.color = Color.argb(255,0,0,255)
        val textHeight = blockSizeInPixels.toFloat()
        val maxWidth = numberHorizontalPixels - blockSizeInPixels
        var textSize = 100f;
        paint.textSize = textSize
        while ( paint.measureText(score)> maxWidth)
        {
            textSize -= 5f
            paint.textSize = textSize
        }
        val x = blockSizeInPixels.toFloat()

        canvas.drawText(
            score,
            x,
            textHeight,
            paint
        )



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

        hit = (horizontalTouchedDP.toInt() == subHorizontalPositionDP) &&
                (verticalTouchedDP.toInt() == subVerticalPositionDP)
        val horizontalGapDP = (horizontalTouchedDP - subHorizontalPositionDP).toInt()
        val verticalGap = (verticalTouchedDP - subVerticalPositionDP).toInt()

        //Distance
        val x = (horizontalGapDP * horizontalGapDP).toFloat() + (verticalGap * verticalGap).toFloat()
        distanceFromSubInDP = sqrt(x).toInt()



        draw()

        if(debugging)
        {
            Log.d("Debugging","In takeShot")
            Log.d("In takeShot","horizontalTouchedDP $horizontalTouchedDP verticalTouchedDP $verticalTouchedDP")
            Log.d("In takeShot","horizontalTouchedPixels $horizontalTouchedInPixels" +
            "verticalTouchedPixels $verticalTouchedInPixels")
            Log.d("hit"," ht: ${horizontalTouchedDP.toInt()} sbh $subHorizontalPositionDP")
            Log.d("hit"," vt: ${verticalTouchedDP.toInt()} sbh $subVerticalPositionDP")
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

        paint.textSize = blockSizeInPixels * 0.6f

        canvas.drawText(
            "numberHorizontalPixels: $numberHorizontalPixels",
            blockSizeInPixels.toFloat(), blockSizeInPixels * 3f, paint )
        canvas.drawText("numberVerticalPixels: $numberVerticalPixels",
            blockSizeInPixels.toFloat(), blockSizeInPixels  * 4f, paint )

        canvas.drawText("blockSizeInPixels: $blockSizeInPixels",
            blockSizeInPixels.toFloat(), blockSizeInPixels * 5f, paint )


        canvas.drawText("gridWidth: $gridWidthInDP",
            blockSizeInPixels.toFloat(), blockSizeInPixels * 6f, paint )

        canvas.drawText("gridHeight: $gridHeightInDP",
            blockSizeInPixels.toFloat(), blockSizeInPixels * 7f, paint )

        canvas.drawText("horizontalTouched: $horizontalTouchedDP",
            blockSizeInPixels.toFloat(), blockSizeInPixels * 8f, paint )

        canvas.drawText("verticalTouched: $verticalTouchedDP",
            blockSizeInPixels.toFloat(), blockSizeInPixels * 9f, paint )

        canvas.drawText("subHorizontalPosition: $subHorizontalPositionDP",
            blockSizeInPixels.toFloat(), blockSizeInPixels * 10f, paint )

        canvas.drawText("subVerticalPosition: $subVerticalPositionDP",
            blockSizeInPixels.toFloat(), blockSizeInPixels * 11f, paint )

        canvas.drawText("hit: $hit",
            blockSizeInPixels.toFloat(), blockSizeInPixels * 12f, paint )

    }
    private fun newGame() {

        subHorizontalPositionDP = Random.nextInt(numberHorizontalDPI/blockSizeInDP)
        subVerticalPositionDP = Random.nextInt(numberVerticalDPI/blockSizeInDP) + 1
        shotsTaken = 0

        if(debugging)
        {

            Log.d("Debugging","In newGame")
            Log.d("In newGame","subHorizontalPositionDP $subHorizontalPositionDP subVerticalPositionDP: $subVerticalPositionDP")
        }

    }


}