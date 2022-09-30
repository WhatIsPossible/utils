package lex.utils.utils

import android.graphics.PointF
import lex.utils.exts.getScreenHeight
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt

object DouglasUtil {

    //        private const val DEFAULT_D_MAX = 0.0005f
    private const val DEFAULT_D_MAX = 0.2f

    /**
     * 数据抽稀
     * @param list List<Entry> - 原始数据
     * @param dMax Float - 抽稀阈值(0.0005f)
     * @return List<Entry>: 抽稀后的数据
     */
    fun compress(list: List<PointF>, dMax: Float = DEFAULT_D_MAX): List<PointF> {
        if (dMax <= 0 || list.size < 3) {
            return list
        }
        val result = mutableListOf<PointF>()
        douglas(list, result, dMax)
        result.run {
            add(0, list.first())//添加收尾两点
            add(list.last())
            sortBy {
                it.x
            }
        }
        return result.distinctBy {
            it.x
        }
    }

    /**
     * Douglas-Peuker算法抽稀
     *
     * Douglas-Peuker算法(DP算法)过程如下:
     * 1、连接曲线首尾两点A、B；
     * 2、依次计算曲线上所有点到A、B两点所在曲线的距离。找出最大距离maxDistance；
     * 3、maxDistance < dMax, 这条曲线上的中间点所有舍去;
     * 4、maxDistance ≥ dMax, 保留maxDistance相应的坐标点,并以该点为界,把曲线分为两部分。
     * 5、这两部分反复重复1~4步骤，直到所有maxDistance均小于阈值。即完成抽稀。
     *
     * 这种算法的抽稀精度与阈值有很大关系，阈值越大，简化程度越大，点减少的越多；反之简化程度越低，点保留的越多，形状也越趋于原曲线。
     *
     * @param list List<PointF> - 原始数据
     * @param resultList MutableList<PointF> - 抽稀后的数据
     * @param dMax Float - 抽稀阈值
     */
    private fun douglas(list: List<PointF>, resultList: MutableList<PointF>, dMax: Float) {
        if (dMax <= 0) {
            resultList.addAll(list)
            return
        }

        if (list.size < 3) return

        val first = list.first()
        val last = list.last()
        var maxValueIndex = -1
        var maxDistance = 0f

        list.forEachIndexed { index, entry ->
            val distance = pointToLineDistance(entry, first, last)
            if (distance > maxDistance) {
                maxDistance = distance
                maxValueIndex = index
            }
        }
        if (maxDistance >= dMax) {
            resultList.add(list[maxValueIndex])

            //将原来的集合，以当前点为中心，分割两段。分别进行递归处理
            val list1 = list.take(maxValueIndex + 1)
            val list2 = list.drop(maxValueIndex)
            douglas(list1, resultList, dMax)
            douglas(list2, resultList, dMax)
        }
    }

    /**
     * 计算两点之间的距离
     * @param point1 PointF
     * @param point2 PointF
     * @return Float: 两点之间的距离
     */
    private fun pointDistance(point1: PointF, point2: PointF): Float {
        val x1 = point1.x
        val y1 = point1.y
        val x2 = point2.x
        val y2 = point2.y

        if (x1 == x2 && y1 == y2) return 0f
        if (x1 == x2) return (y1 - y2).absoluteValue
        if (y1 == y2) return (x1 - x2).absoluteValue
        //勾股定理
        return sqrt((x1 - x2).absoluteValue.pow(2) + (y1 - y2).absoluteValue.pow(2))
    }

    /**
     * 计算点到 另外两点所在曲线的距离
     * @param point PointF
     * @param startPoint PointF - 曲线开始点
     * @param endPoint PointF - 曲线结束点
     * @return Float: point到曲线的距离
     */
    private fun pointToLineDistance(point: PointF, startPoint: PointF, endPoint: PointF): Float {
        val a = pointDistance(startPoint, endPoint)
        val b = pointDistance(point, startPoint)
        val c = pointDistance(point, endPoint)

        val p = (a + b + c) / 2
        val s = sqrt(p * (p - a) * (p - b) * (p - c))//海伦公式
        return 2 * s / a//三角形面积公式 S=ah/2
    }
}