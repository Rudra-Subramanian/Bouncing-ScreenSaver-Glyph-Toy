package com.nothinglondon.sdkdemo.demos.animation

import android.content.Context
import com.nothing.ketchum.GlyphMatrixManager
import com.nothinglondon.sdkdemo.demos.GlyphMatrixService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

class boundary(var width: Int, var height: Int){
    var mygrid = Array(HEIGHT * WIDTH) { 0 }
    var leftwall : Int = 7
    var rightwall : Int = 17
    var topwall : Int = 5
    var bottomwall: Int = 19

    init {
        if (width < 3){ //making sure min value of 3
            width = 3
        }
        else if (width % 2 == 0){ //making sure value is odd
            width -= 1
        }
        if (height < 3){ // making sure min value of 3
            height = 3
        }
        else if (height % 2 == 0){ //making sure value is odd
            height -= 1
        }

        var leftwall = MID_POINT - ((width - 1) / 2)
        var rightwall = MID_POINT + ((width - 1) / 2)
        var topwall = MID_POINT - ((height - 1) / 2)
        var bottomwall = MID_POINT + ((height - 1) / 2)

        create_wall_grid()
    }
    fun create_wall_grid(){
        for (i in 0..<WIDTH){
           for (j in  0..<HEIGHT){
               if (((i > rightwall) or (i < leftwall)) or ((j > bottomwall) or (j < topwall))){
                   mygrid[i * WIDTH + j] = 255 // max brightness
                   //setting 2d array value in 1d array
               }
           }
        }

    }
    private companion object {
        private const val WIDTH = 25
        private const val HEIGHT = 25
        private const val HALF_HEIGHT = HEIGHT.toDouble() / 2
        private const val MID_POINT = HEIGHT / 2
        private const val ANGLE_PER_PIXEL_DEGREES = 360.0 / WIDTH
    }
}

class ball(var xspeed: Int, var yspeed: Int){
    var xpos : Int = 15
    var ypos : Int = 10
    var isRunning : Boolean = false
    var wall = boundary(8,9)

    fun check_bounce(nextframevalues: Array<Int>): Array<Int>{
        var corrected_next_frame = nextframevalues
        if (nextframevalues[0] > wall.rightwall){
            corrected_next_frame[0] = wall.rightwall
            xspeed = -xspeed
        }
        else if (nextframevalues[0] < wall.leftwall){
            corrected_next_frame[0] = wall.leftwall
            xspeed = -xspeed
        }
        if (nextframevalues[1] > wall.bottomwall){
            corrected_next_frame[1] =  wall.bottomwall
            yspeed = -yspeed
        }
        else if (nextframevalues[1] <  wall.topwall){
            corrected_next_frame[1] = wall.topwall
            yspeed = -yspeed
        }
        return corrected_next_frame
    }
    
    fun get_next_pos(): Array<Int>{
        val newxpos = xpos + xspeed
        val newypos = ypos + yspeed
        return arrayOf(newxpos, newypos)
    }
    
    
    fun get_next_frame(){
        var nextframevalues = get_next_pos()
        nextframevalues = check_bounce(nextframevalues)
        
        xpos = nextframevalues[0]
        ypos = nextframevalues[1]
    }
    
    
}


class BouncingBallToy : GlyphMatrixService("Bouncing-Ball") {
    private val backgroundScope = CoroutineScope(Dispatchers.IO)
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private var frame = 0
    private var newball = ball(2,1)

    override fun onTouchPointLongPress() {
        println("change something")

    }



    override fun performOnServiceConnected(
        context: Context,
        glyphMatrixManager: GlyphMatrixManager
    ) {
        backgroundScope.launch {
            while (isActive) {
                val array = generateNextAnimationFrame()
                uiScope.launch {
                    glyphMatrixManager.setMatrixFrame(array)
                }
                // wait a bit
                delay(60)
                // next frame
                frame++
                if (frame >= WIDTH) {
                    frame = 0
                }
            }
        }
    }

    override fun performOnServiceDisconnected(context: Context) {
        backgroundScope.cancel()
    }

    private fun create_ball_grid() : Array<Int>{
        var ball_grid = Array(HEIGHT * WIDTH) { 0 }
        //get ball position as first point
        ball_grid[newball.xpos * WIDTH + newball.ypos] = 255
        //point behind x
        var lagginxgpos = newball.xpos - (newball.xspeed / newball.xspeed)
        //point behind y
        var laggingypos = newball.ypos - (newball.yspeed/newball.yspeed)
        ball_grid[newball.xpos * WIDTH + laggingypos] = 255
        ball_grid[lagginxgpos * WIDTH + newball.ypos] = 255
        //point diagonal behind both
        ball_grid[lagginxgpos * WIDTH + laggingypos] = 255
        return ball_grid
    }

    private fun generateNextAnimationFrame(): IntArray {
        // Get ball position
        newball.get_next_frame()
        val ball_x_position = newball.xpos
        val ball_y_position = newball.ypos

        val ball_drawing = create_ball_grid()
        val border_drawing = newball.wall.mygrid


        // Start with every LED turned off.
        var grid = Array(HEIGHT * WIDTH) { 0 }

        grid.forEachIndexed{ index, value1 ->
            grid[index] = maxOf(ball_drawing[index], border_drawing[index])
        }


        return grid.toIntArray()
    }

    private companion object {
        private const val WIDTH = 25
        private const val HEIGHT = 25
        private const val HALF_HEIGHT = HEIGHT.toDouble() / 2
        private const val MID_POINT = HEIGHT / 2
        private const val ANGLE_PER_PIXEL_DEGREES = 360.0 / WIDTH
    }

}
