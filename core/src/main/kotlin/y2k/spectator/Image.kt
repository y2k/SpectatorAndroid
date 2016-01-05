package y2k.spectator

/**
 * Created by y2k on 1/5/16.
 */
data class Image(
    val id: Int,
    val width: Int = 0,
    val height: Int = 0) {

    val isEmpty = width == 0 || height == 0

    val isDefault = id == -1

    fun normalize(width: Int, height: Int): Image {
        return if (isDefault) this else Image(id, width, height)
    }

    companion object {

        val Default = Image(-1)
    }
}