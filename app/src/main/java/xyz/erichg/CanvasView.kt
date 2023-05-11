package xyz.erichg
import android.app.Activity
import android.content.Context
import android.graphics.*
import android.media.AudioAttributes
import android.media.SoundPool
import android.media.SoundPool.Builder
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random

class CanvasView( context: Context) : View(context){

    private var debugging: Boolean = true
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap

    private var background = ResourcesCompat.getColor(resources,R.color.teal_200,null)
    private val titleBar = ResourcesCompat.getColor(resources,R.color.purple_200,null)

    private var density : Float = 0f
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
    private var _shotsTaken: Int = 0
    private var distanceFromSubInPixels: Int = -1
    private var distanceFromSubInDP: Int = -1

    private var paint: Paint = Paint()
    private var move: Rect = Rect()
    private var previousMove = Rect()


    var distance = 0
    var shotsTaken = 0
    private var soundPool: SoundPool? = null
    private var explosionSoundId = 0

    init {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setAudioAttributes(attributes).build()
        explosionSoundId = soundPool?.load(context, R.raw.explosion,1) ?: 0


    } fun setDebug()
    {
        debugging = !debugging
        if(!debugging)
        {
            setBackgroundColor(background)
        }
        else
        {
            invalidate()
        }

        Log.d("setdebug","$debugging ")
    }
    var isContextMenuOpen = false
    var isContextMenuClosed = false
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if(isContextMenuOpen or isContextMenuClosed)
        {
            if(isContextMenuClosed)
            {
                isContextMenuClosed = false
            }
            Log.d("onsizecahnged returnung" ,"$isContextMenuOpen")
            return
        }

        if(::extraBitmap.isInitialized) extraBitmap.recycle()



        extraBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)

        extraCanvas.drawColor(background)


        numberHorizontalPixels = w
        numberVerticalPixels = h


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


        newGame()
        if( debugging)
        {
            Log.d("onSizeChanged"," w: $numberHorizontalPixels h: $numberVerticalPixels")
            Log.d("blockSizeInPixels","$blockSizeInPixels")
            Log.d("gridwidthDP : $gridWidthInDP","gridHeigth $gridHeightInDP")
        }


    }



    override fun onDraw(canvas: Canvas)
    {
        super.onDraw(canvas)

        canvas.drawBitmap(extraBitmap,0f,0f,null)

        if(debugging)
        {
            Log.d("debugging","printdebiggfunction")
            printDebuggingText()
        }

        background()
        drawGrid()
        drawShot()
        drawScore()




    }

    override fun setBackgroundColor(color: Int) {
        background = color
        extraCanvas.drawColor(background)
        Toast.makeText(context, "Background color changed", Toast.LENGTH_SHORT).show()
        invalidate()
    }

    private fun background()
    {

        paint.color = background
        extraCanvas.drawRect(previousMove,paint)

    }

    private fun drawGrid()
    {
        paint.color = Color.argb(255,0,0,0)

        // Draw the horizontal lines of the grid
        for ( i in 0 until gridHeightInDP)
        {
            extraCanvas.drawLine(0f,
                blockSizeInPixels * i.toFloat(),
                numberHorizontalPixels.toFloat(),
                blockSizeInPixels * i.toFloat(),
                paint)

        }

        // Draw the horizontal lines of the grid
        for( i in 0 until gridWidthInDP)
        {
            extraCanvas.drawLine(blockSizeInPixels * i.toFloat(),
                0f,
                blockSizeInPixels * i.toFloat(),
                numberVerticalPixels.toFloat(),
                paint)
        }
    }
    override fun onTouchEvent(event: MotionEvent?): Boolean {


        if( event?.action?.and(MotionEvent.ACTION_MASK)
            == MotionEvent.ACTION_UP)
        {

            Log.d("menuopen","$isContextMenuOpen")
            if(isContextMenuOpen)
            {
                return true
            }
            takeShot(event.x,event.y)
        }
        if(debugging)
        {
            Log.d("Debugging","In onTouchEvent")
            Log.d("event.x ${event?.x}","event.y ${event?.y}")
        }

        return super.onTouchEvent(event)
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
        if(hit)
        {
            Log.d("takeShot","call gameOver")
            gameOver()
        }
        else {

            invalidate()
        }





        if(debugging)
        {
            Log.d("Debugging","In takeShot")
            Log.d("In takeShot","horizontalTouchedDP $horizontalTouchedDP verticalTouchedDP $verticalTouchedDP")
            Log.d("In takeShot","horizontalTouchedPixels $horizontalTouchedInPixels" +
                    "verticalTouchedPixels $verticalTouchedInPixels")
            Log.d("hit $hit "," ht: ${horizontalTouchedDP.toInt()} sbh $subHorizontalPositionDP")
            Log.d("hit $hit"," vt: ${verticalTouchedDP.toInt()} sbh $subVerticalPositionDP")
        }




    }

    private fun gameOver(){

        val act = context as MainActivity
        val gameOverFragment = GameOverFragment()
        val fragmentManager = act.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container,gameOverFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }



    private fun  drawScore()
    {


        paint.style = Paint.Style.FILL
        paint.alpha = 255
        paint.color = titleBar



        extraCanvas.drawRect(0f,0f,
            (gridWidthInDP * blockSizeInPixels).toFloat(), blockSizeInPixels.toFloat(),paint)
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

        extraCanvas.drawText(
            score,
            x,
            textHeight,
            paint
        )



    }

    private fun drawShot()
    {

        soundPool?.play(explosionSoundId, 1f,1f,1,0,1f)
        paint.color = Color.BLACK
        val left = horizontalTouchedDP * blockSizeInPixels
        val top = verticalTouchedDP * blockSizeInPixels
        val right = (horizontalTouchedDP * blockSizeInPixels) + blockSizeInPixels
        val bottom = (verticalTouchedDP * blockSizeInPixels) + blockSizeInPixels

        if ( verticalTouchedDP == 0f || verticalTouchedDP == (numberVerticalDPI/ blockSizeInDP).toFloat() ) return

        move.set(left.toInt(),top.toInt(),right.toInt(),bottom.toInt())
        previousMove.set(move)

        extraCanvas.drawRect( move, paint )

    }

    private fun newGame() {

        subHorizontalPositionDP = Random.nextInt(numberHorizontalDPI/blockSizeInDP)
        subVerticalPositionDP = Random.nextInt(numberVerticalDPI/blockSizeInDP)+1
        shotsTaken = 0

        if(debugging)
        {

            Log.d("Debugging","In newGame")
            Log.d("In newGame","subHorizontalPositionDP $subHorizontalPositionDP subVerticalPositionDP: $subVerticalPositionDP")
        }

    }
    // This code prints the debugging text
    // to the device's screen
    private fun printDebuggingText() {

        paint.textSize = blockSizeInPixels * 0.6f

        var hp = "numberHorizontalPixels: $numberHorizontalPixels"
        val textWith = paint.measureText(hp)
        //paint.color = Color.RED
        paint.color = background

        extraCanvas.drawRect(blockSizeInPixels.toFloat(),
            blockSizeInPixels * 2f,textWith + blockSizeInPixels,
            ( 12f* blockSizeInPixels),paint)

        paint.color = Color.BLUE
        extraCanvas.drawText(
            hp,
            blockSizeInPixels.toFloat(), blockSizeInPixels * 3f, paint )
        extraCanvas.drawText("numberVerticalPixels: $numberVerticalPixels",
            blockSizeInPixels.toFloat(), blockSizeInPixels  * 4f, paint )

        extraCanvas.drawText("blockSizeInPixels: $blockSizeInPixels",
            blockSizeInPixels.toFloat(), blockSizeInPixels * 5f, paint )


        extraCanvas.drawText("gridWidth: $gridWidthInDP",
            blockSizeInPixels.toFloat(), blockSizeInPixels * 6f, paint )

        extraCanvas.drawText("gridHeight: $gridHeightInDP",
            blockSizeInPixels.toFloat(), blockSizeInPixels * 7f, paint )

        extraCanvas.drawText("horizontalTouched: $horizontalTouchedDP",
            blockSizeInPixels.toFloat(), blockSizeInPixels * 8f, paint )

        extraCanvas.drawText("verticalTouched: $verticalTouchedDP",
            blockSizeInPixels.toFloat(), blockSizeInPixels * 9f, paint )

        extraCanvas.drawText("subHorizontalPosition: $subHorizontalPositionDP",
            blockSizeInPixels.toFloat(), blockSizeInPixels * 10f, paint )

        extraCanvas.drawText("subVerticalPosition: $subVerticalPositionDP",
            blockSizeInPixels.toFloat(), blockSizeInPixels * 11f, paint )

        extraCanvas.drawText("hit: $hit",
            blockSizeInPixels.toFloat(), blockSizeInPixels * 12f, paint )

    }



}