package com.distantlandgames.tetrisandwords.tetris

import android.util.Log
import com.distantlandgames.violet.Vector2
import com.distantlandgames.violet.Vector3
import com.distantlandgames.violet.`object`.Instance3D
import com.distantlandgames.violet.helpers.InstanceData
import java.security.acl.Group
import java.util.*

fun ClosedRange<Float>.random() = Random().nextFloat()

class GroupCell : Instance3D() {
    var localPos: Vector2 = Vector2()
    var offset: Vector3 = Vector3()
    var letter: String = ""
    var empty: Boolean = true
}

class TetrisGroup {
    var rotationIndices = intArrayOf(0, 1, 2, 3)
    var rotations: Vector<Vector<Vector2>>
    var cells = arrayOf(
        GroupCell(),
        GroupCell(),
        GroupCell(),
        GroupCell()
    )
    var currentRotation: Int = 0
    var groupPosition: Vector2

    init {
        groupPosition = Vector2(0f, 0f)
        rotations = Vector<Vector<Vector2>>()
        for(i in 0 .. 4) {
            rotations.add(Vector<Vector2>())
            for(j in 0 .. 4) {
                rotations.elementAt(i).addElement(Vector2())
            }
        }

        cells[0].letter = "d"
        cells[1].letter = "o"
        cells[2].letter = "n"
        cells[3].letter = "t"
    }

    fun move(direction: Vector2) {
        groupPosition.x += direction.x
        groupPosition.y += direction.y
    }

    fun moveUp() {
        groupPosition.y -= 1f
    }

    fun moveDown() {
        groupPosition.y += 1f
    }

    fun moveLeft() {
        groupPosition.x -= 1f
    }

    fun moveRight() {
        groupPosition.x += 1f
    }

    fun rotateRight() {
        currentRotation++
        var newRotation = currentRotation % rotationIndices.size
        var rotationIndex = rotationIndices[Math.abs(newRotation)]

        for((index, cell) in cells.withIndex()) {
            cell.localPos.x = rotations[rotationIndex][index].x
            cell.localPos.y = rotations[rotationIndex][index].y
        }

        currentRotation = newRotation
    }

    fun rotateLeft() {
        currentRotation--
        var newRotation = currentRotation % rotationIndices.size
        var rotationIndex = rotationIndices[Math.abs(newRotation)]

        for((index, cell) in cells.withIndex()) {
            cell.localPos.x = rotations[rotationIndex][index].x
            cell.localPos.y = rotations[rotationIndex][index].y
        }

        currentRotation = newRotation
    }

    fun setRotation(index: Int, cellTL: Vector2, cellTR: Vector2, cellBL: Vector2, cellBR: Vector2) {
        rotations[index][0].x = cellTL.x
        rotations[index][0].y = cellTL.y
        rotations[index][1].x = cellTR.x
        rotations[index][1].y = cellTR.y
        rotations[index][2].x = cellBL.x
        rotations[index][2].y = cellBL.y
        rotations[index][3].x = cellBR.x
        rotations[index][3].y = cellBR.y
    }

    fun setGroupO() {
        cells[0].localPos.x = 0f
        cells[0].localPos.y = 0f

        cells[1].localPos.x = 1f
        cells[1].localPos.y = 0f

        cells[2].localPos.x = 0f
        cells[2].localPos.y = 1f

        cells[3].localPos.x = 1f
        cells[3].localPos.y = 1f

        setRotation(0, Vector2(0f, 0f), Vector2(1f, 0f), Vector2(0f, 1f), Vector2(1f, 1f))
        setRotation(1, Vector2(1f, 0f), Vector2(1f, 1f), Vector2(0f, 0f), Vector2(0f, 1f))
        setRotation(2, Vector2(1f, 1f), Vector2(0f, 1f), Vector2(1f, 0f), Vector2(0f, 0f))
        setRotation(3, Vector2(0f, 1f), Vector2(0f, 0f), Vector2(1f, 1f), Vector2(1f, 0f))
    }

    fun setGroupL() {
        cells[0].localPos.x = 0f
        cells[0].localPos.y = 0f

        cells[1].localPos.x = 0f
        cells[1].localPos.y = 1f

        cells[2].localPos.x = 0f
        cells[2].localPos.y = 2f

        cells[3].localPos.x = 1f
        cells[3].localPos.y = 2f

        setRotation(0, Vector2(0f, -1f), Vector2(0f, 0f), Vector2(0f, 1f), Vector2(1f, 1f))
        setRotation(1, Vector2(1f, 0f), Vector2(0f, 0f), Vector2(-1f, 0f), Vector2(-1f, 1f))
        setRotation(2, Vector2(0f, 1f), Vector2(0f, 0f), Vector2(0f, -1f), Vector2(-1f, -1f))
        setRotation(3, Vector2(-1f, 0f), Vector2(0f, 0f), Vector2(1f, 0f), Vector2(1f, -1f))
    }

    fun setGroupLReverse() {
        cells[0].localPos.x = 0f
        cells[0].localPos.y = 0f

        cells[1].localPos.x = 0f
        cells[1].localPos.y = 1f

        cells[2].localPos.x = 0f
        cells[2].localPos.y = 2f

        cells[3].localPos.x = -1f
        cells[3].localPos.y = 2f

        setRotation(0, Vector2(0f, -1f), Vector2(0f, 0f), Vector2(0f, 1f), Vector2(-1f, 1f))
        setRotation(1, Vector2(1f, 0f), Vector2(0f, 0f), Vector2(-1f, 0f), Vector2(-1f, -1f))
        setRotation(2, Vector2(0f, 1f), Vector2(0f, 0f), Vector2(0f, -1f), Vector2(1f, -1f))
        setRotation(3, Vector2(-1f, 0f), Vector2(0f, 0f), Vector2(1f, 0f), Vector2(1f, 1f))
    }

    fun setGroupT() {
        cells[0].localPos.x = 0f
        cells[0].localPos.y = 0f

        cells[1].localPos.x = 0f
        cells[1].localPos.y = 1f

        cells[2].localPos.x = -1f
        cells[2].localPos.y = 1f

        cells[3].localPos.x = 1f
        cells[3].localPos.y = 1f

        setRotation(0, Vector2(0f, -1f), Vector2(-1f, 0f), Vector2(0f, 0f), Vector2(1f, 0f))
        setRotation(1, Vector2(1f, 0f), Vector2(0f, -1f), Vector2(0f, 0f), Vector2(0f, 1f))
        setRotation(2, Vector2(0f, 1f), Vector2(1f, 0f), Vector2(0f, 0f), Vector2(-1f, 0f))
        setRotation(3, Vector2(-1f, 0f), Vector2(0f, 1f), Vector2(0f, 0f), Vector2(0f, -1f))
    }

    fun setGroupLong() {
        cells[0].localPos.x = 0f
        cells[0].localPos.y = 0f

        cells[1].localPos.x = 0f
        cells[1].localPos.y = 1f

        cells[2].localPos.x = 0f
        cells[2].localPos.y = 2f

        cells[3].localPos.x = 0f
        cells[3].localPos.y = 3f

        setRotation(0, Vector2(0f, -1f), Vector2(0f, 0f), Vector2(0f, 1f), Vector2(0f, 2f))
        setRotation(1, Vector2(2f, 0f), Vector2(1f, 0f), Vector2(0f, 0f), Vector2(-1f, 0f))
        setRotation(2, Vector2(0f, 2f), Vector2(0f, 1f), Vector2(0f, 0f), Vector2(0f, -1f))
        setRotation(3, Vector2(-1f, 0f), Vector2(0f, 0f), Vector2(1f, 0f), Vector2(2f, 0f))
    }

    fun setGroupGuss() {
        cells[0].localPos.x = 0f
        cells[0].localPos.y = 0f
        cells[0].letter = "t"

        cells[1].localPos.x = 1f
        cells[1].localPos.y = 0f
        cells[1].letter = "o"

        cells[2].localPos.x = -1f
        cells[2].localPos.y = 1f
        cells[2].letter = "m"

        cells[3].localPos.x = 0f
        cells[3].localPos.y = 1f
        cells[3].letter = "j"

        setRotation(0, Vector2(0f, 0f), Vector2(1f, 0f), Vector2(-1f, 1f), Vector2(0f, 1f))
        setRotation(1, Vector2(0f, 0f), Vector2(0f, 1f), Vector2(-1f, -1f), Vector2(-1f, 0f))
        setRotation(2, Vector2(0f, 1f), Vector2(-1f, 1f), Vector2(1f, 0f), Vector2(0f, 0f))
        setRotation(3, Vector2(-1f, 0f), Vector2(-1f, -1f), Vector2(0f, 1f), Vector2(0f, 0f))
    }

    fun setGroupGussReverse() {
        cells[0].localPos.x = -1f
        cells[0].localPos.y = 0f

        cells[1].localPos.x = 0f
        cells[1].localPos.y = 0f

        cells[2].localPos.x = 1f
        cells[2].localPos.y = 1f

        cells[3].localPos.x = 0f
        cells[3].localPos.y = 1f

        setRotation(0, Vector2(-1f, 0f), Vector2(0f, 0f), Vector2(0f, 1f), Vector2(1f, 1f))
        setRotation(1, Vector2(1f, -1f), Vector2(1f, 0f), Vector2(0f, 0f), Vector2(0f, 1f))
        setRotation(2, Vector2(1f, 1f), Vector2(0f, 1f), Vector2(0f, 0f), Vector2(-1f, 0f))
        setRotation(3, Vector2(-1f, 1f), Vector2(-1f, 0f), Vector2(0f, 0f), Vector2(0f, -1f))
    }

}

open class GroupSpawner {
    var allLetters = charArrayOf('q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm')
    var allWords: Vector<Vector<String>> = Vector<Vector<String>>()

    fun spawnGroup(): TetrisGroup {
        var group: TetrisGroup =
            TetrisGroup()

        for(cell in group.cells) {
            val id = (0 .. allLetters.size).random()
            var letter = 'o'
            if(id < allLetters.size) {
                letter = allLetters[id]
            }
            cell.letter = letter.toString()
        }

        // Randomize character

        return group
    }
}

class TetrisGameLogic() {
    //private var tetrisBoard: TetrisBoard
    private var currenGroup: TetrisGroup =
        TetrisGroup()

    private var groupSpawner: GroupSpawner =
        GroupSpawner()
    private var activeGroup =
        TetrisGroup()

    private var boardOriginWithStates: Vector<Vector<GroupCell>> = Vector()

    private var onInstanceRearranged: (instanceFrom: GroupCell, instanceTo: GroupCell) -> Unit
            = { instanceFrom, instanceTo -> }

    private var onNewInstanceAdded: (instance: GroupCell) -> Unit
            = { instance -> }

    private var onInstanceRemoved: (instance: GroupCell) -> Unit
            = { instance -> }

    private var onRowFull: (indicesToFullRows: Vector<Int>) -> Unit
            = { row -> }

    private var onBlockMoving: (cell: GroupCell) -> Unit = { cell ->  }

    private var onLoseGame: () -> Unit = {}

    private var isGroupNew = true

    init {

    }

    fun createNewGame(nrOfColumns: Int, nrOfRows: Int) {
        this.currenGroup =
            TetrisGroup()
        this.groupSpawner =
            GroupSpawner()

        boardOriginWithStates.clear()

        for(r in 0 until nrOfRows) {
            boardOriginWithStates.addElement(Vector<GroupCell>())
            for (c in 0 until nrOfColumns) {
                var cell =
                    GroupCell()
                boardOriginWithStates[r].addElement(cell)

                cell.letter = ""
                cell.empty = true
            }
        }

        placeAsPlane(boardOriginWithStates, nrOfColumns, nrOfRows, 1f, Vector3(0f, 0f, -10f))

        spawnNewGroup()
    }

    fun loadExistingGame(board: Vector<Vector<GroupCell>>) {
        Log.d("VIOLET", "Loading existing game")
        var nrOfRows: Int = board.count()
        var nrOfColumns: Int = board[0].count()

        this.currenGroup =
            TetrisGroup()
        this.groupSpawner =
            GroupSpawner()

        createNewGame(nrOfColumns, nrOfRows)

        for(r in 0 until nrOfRows) {
            for (c in 0 until nrOfColumns) {
                boardOriginWithStates[r][c].letter = getCellFromBoard(board, c, r).letter
                boardOriginWithStates[r][c].empty = getCellFromBoard(board, c, r).empty
            }
        }

        placeAsPlane(boardOriginWithStates, nrOfColumns, nrOfRows, 1f, Vector3(0f, 0f, -10f))
    }

    fun getCell(column: Int, row: Int) = boardOriginWithStates[row][column]

    fun getCellFromBoard(board: Vector<Vector<GroupCell>>, column: Int, row: Int) = board[row][column]

    fun setOnInstanceChangedPos(callback: (GroupCell, GroupCell) -> Unit) {
        onInstanceRearranged = callback
    }

    fun setOnInstanceAdded(callback: (GroupCell) -> Unit) {
        onNewInstanceAdded = callback
    }

    fun setOnInstanceRemoved(callback: (GroupCell) -> Unit) {
        onInstanceRemoved = callback
    }

    fun setOnRowFull(callback: (Vector<Int>) -> Unit) {
        onRowFull = callback
    }

    fun setOnLoseGame(callback: () -> Unit) {
        onLoseGame = callback
    }

    fun setOnBlockMove(callback: (group: GroupCell) -> Unit) {
        onBlockMoving = callback
    }

    fun updateMovement() {
        removeFromBoard(activeGroup)
        activeGroup.moveDown()
        if(isValidGridPos(activeGroup)) {
            applyToBoard(activeGroup)
        }
        else {
            activeGroup.moveUp()
            applyToBoard(activeGroup)

            scanForFullRows()

            spawnNewGroup()
        }
    }

    private fun <T : Instance3D>placeAsPlane(plane: Vector<Vector<T>>,
                                             nrOfColumns: Int,
                                             nrOfRows: Int,
                                             blockSize: Float, offset: Vector3 = Vector3()) {

        var startPosX: Float = (blockSize*nrOfColumns.toFloat()) * -0.5f + blockSize * 0.5f
        var startPosY: Float = (blockSize*nrOfRows.toFloat()) * 0.5f + blockSize * 0.5f

        for(r in 0 until nrOfRows) {
            for (c in 0 until nrOfColumns) {
                var planeBit = plane[r][c]

                planeBit.position = Vector3(startPosX + offset.x,
                    startPosY + offset.y, offset.z)

                planeBit.rotationAxis = Vector3(0f, 1f, 1f)
                planeBit.rotationAngle = 0f

                startPosX += blockSize
            }

            startPosX = (blockSize*nrOfColumns.toFloat()) * -0.5f + blockSize * 0.5f
            startPosY -= blockSize
        }
    }

    fun moveDown(touchId: Int) {
        removeFromBoard(activeGroup)
        //testInstance.position.y += 1f
        activeGroup.moveDown()
        if(!isValidGridPos(activeGroup)) {
            activeGroup.moveUp()
        }

        applyToBoard(activeGroup)
    }

    fun moveLeft(touchId: Int) {
        removeFromBoard(activeGroup)
        //testInstance.position.x -= 1f
        activeGroup.moveLeft()
        Log.d("VIOLET", "Moved left!")
        if(!isValidGridPos(activeGroup)) {
            activeGroup.moveRight()
            Log.d("VIOLET", "Reversing left!")
        }

        applyToBoard(activeGroup)
    }

    fun rotateLeft(touchId: Int) {
        removeFromBoard(activeGroup)
        //testInstance.position.x -= 1f
        activeGroup.rotateLeft()
        Log.d("VIOLET", "Rotate left!")
        if(!isValidGridPos(activeGroup)) {
            activeGroup.rotateRight()
            Log.d("VIOLET", "Reversing left rotation!")
        }

        applyToBoard(activeGroup)
    }

    fun rotateRight(touchId: Int) {
        removeFromBoard(activeGroup)
        //testInstance.position.x -= 1f
        activeGroup.rotateRight()
        Log.d("VIOLET", "Rotate right!")
        if(!isValidGridPos(activeGroup)) {
            activeGroup.rotateLeft()
            Log.d("VIOLET", "Reversing right rotation!")
        }

        applyToBoard(activeGroup)
    }

    fun moveRight(touchId: Int) {
        removeFromBoard(activeGroup)
        //testInstance.position.x += 1f
        activeGroup.moveRight()
        Log.d("VIOLET", "Moved right!")
        if(!isValidGridPos(activeGroup)) {
            activeGroup.moveLeft()
            Log.d("VIOLET", "Reversing right!")
        }

        applyToBoard(activeGroup)
    }

    fun scanForFullRows() {
        var nrOfRows = boardOriginWithStates.size
        var rowIndex = nrOfRows-1

        var allRows: Vector<Int> = Vector()

        while(rowIndex >= 0) {
            var row = getRow(rowIndex)
            if(isRowFull(rowIndex)) {

                allRows.add(rowIndex)
                var word = getRowString(rowIndex)


                //doAsync {
                //    var result = calculatePointsFrom(word)
                //    Log.d("VIOLET", "Calculated points: " + result)
                //}.execute()
//
                //gameState = 1
                //rowIndicesToBeDeleted.add(rowIndex)

//
                //if(rowIndex >= animationRowStart) {
                //    animationRowStart = rowIndex
                //}
            }
            rowIndex--
        }

        //if(rowIndicesToBeDeleted.size > 0) {
        //    removeFromBoard(activeGroup)
        //    gameState = 1
        //    fallAnimation.reset()
        //}
        if(allRows.size > 0) {
            onRowFull(allRows)
        }
    }

    fun getRow(rowIndex: Int): Vector<GroupCell> {
        return boardOriginWithStates[rowIndex]
    }

    fun decreaseRowsAbove(rowStart: Int) {
        for(rowIndex in rowStart downTo 0) {
            decreaseRow(rowIndex)
        }
    }

    fun getBoardInstances() = boardOriginWithStates

    fun nrOfRows() = boardOriginWithStates.count()

    fun nrOfColumns() = boardOriginWithStates[0].count()

    fun insideBorder(column: Int, row: Int): Boolean {
        return ((column >= 0 && column < boardOriginWithStates[0].count())
                && (row >= 0 && row < boardOriginWithStates.count()))
    }

    fun insideBorder(v: Vector2, offset: Vector2): Boolean {
        return (((v.x+offset.x) >= 0 && (v.x+offset.x) < nrOfColumns())
                && ((v.y+offset.y) >= 0 && (v.y+offset.y) < nrOfRows()))
    }

    fun insideBorder(v: Vector2): Boolean {
        return ((v.x >= 0 && v.x < nrOfColumns())
                && (v.y >= 0 && v.y < nrOfRows()))
    }

    fun getRowString(rowIndex: Int): String {
        var word = ""
        var row = getRow(rowIndex)

        for((coulumnIndex, column) in row.withIndex()) {
            word += column.letter
        }

        return word
    }

    fun removeActiveGroupFromBoard() {
        if(activeGroup != null) {
            removeFromBoard(activeGroup)
        }
    }

    fun applyActiveGroupFromBoard() {
        if(activeGroup != null) {
            applyToBoard(activeGroup)
        }
    }

    fun spawnNewGroup() {
        activeGroup = groupSpawner.spawnGroup()

        var nr = (0..6).random()

        when(nr) {
            0 -> {
                activeGroup.setGroupGuss()
            }
            1 -> {
                activeGroup.setGroupGussReverse()
            }
            2 -> {
                activeGroup.setGroupLong()
            }
            3 -> {
                activeGroup.setGroupO()
            }
            4 -> {
                activeGroup.setGroupL()
            }
            5 -> {
                activeGroup.setGroupLReverse()
            }
            6 -> {
                activeGroup.setGroupT()
            }
        }

        activeGroup.move(Vector2(3f, 3f))
        activeGroup.rotateRight()

        if(!isValidGridPos(activeGroup)) {
            for(cell in activeGroup.cells) {
                Log.d("VIOLET", "Cell in group: ${cell.localPos.x} ${cell.localPos.y}")
            }
            Log.d("VIOLET", "Game over!")
            if(boardOriginWithStates.size == 0) {
                Log.d("VIOLET", "Bugged! board is empty")
            }
            onLoseGame()
        }
    }

    private fun applyToBoard(group: TetrisGroup) {
        for((index, groupCell) in group.cells.withIndex()) {
            var x = (group.groupPosition.x + groupCell.localPos.x).toInt()
            var y = (group.groupPosition.y + groupCell.localPos.y).toInt()

            if(insideBorder(x, y)) {
                var gridCell = getCell(x, y)

                gridCell.empty = false
                gridCell.letter = groupCell.letter


                onNewInstanceAdded(gridCell)

                //for(cell in activeGroup.cells) {
                //
                //}
            }
            else {
                Log.d("VIOLET", "Block is outside border: $x $y")
            }
        }
        isGroupNew = false
    }

    private fun removeFromBoard(group: TetrisGroup) {
        if(isGroupNew)
            return
        for(cell in group.cells) {
            var x = (group.groupPosition.x + cell.localPos.x).toInt()
            var y = (group.groupPosition.y + cell.localPos.y).toInt()

            var gridCell = getCell(x, y)

            if(gridCell.empty == false) {
                onInstanceRemoved(gridCell)
            }

            if(gridCell.empty == false) {
                gridCell.empty = true
                gridCell.letter = "_"
            }
        }
    }

    private fun isValidGridPos(group: TetrisGroup): Boolean {
        for(cell in group.cells) {
            var column = (group.groupPosition.x + cell.localPos.x).toInt()
            var row = (group.groupPosition.y + cell.localPos.y).toInt()

            if(!insideBorder(cell.localPos, group.groupPosition)) {
                return false
            }

            if(getCell(column, row).empty == false) {
                return false
            }
        }

        return true
    }

    private fun isRowFull(rowIndex: Int): Boolean {
        var row = getRow(rowIndex) //grid.grid[rowIndex]

        for((coulumnIndex, column) in row.withIndex()) {
            if(column.empty) {
                return false
            }
        }
        return true
    }

    private fun clearRow(rowIndex: Int) {
        var row = getRow(rowIndex)

        for((coulumnIndex, column) in row.withIndex()) {
            column.empty = true
        }
    }

    private fun decreaseRow(rowIndex: Int) {
        if(rowIndex > 0) {
            //var previousRow = getRow(rowIndex-1)
            var row = getRow(rowIndex)
            for((columnIndex, cell) in row.withIndex()) {
                var previousCell = getCell(columnIndex, rowIndex-1)

                onInstanceRearranged(previousCell, cell)

                cell.empty = previousCell.empty
                cell.letter = previousCell.letter

                /// First instances of full row are removed.
                /// Then decreasing row, previous instances are removed,
                /// but the new instances needs to be added.
                //previousRow[columnIndex].empty = true
                //if(previousCell.empty == false) {
                //    removeInstance(previousCell.letter, previousCell)
                //}
                //if(cell.empty == false) {
                //    /// Unlike previous way, now we're only interested in non-empty instances.
                //    addInstnace(cell.letter, cell)
                //}

                previousCell.empty = true
                previousCell.letter = "_"
            }
        }
    }
}