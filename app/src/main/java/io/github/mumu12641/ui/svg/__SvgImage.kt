package io.github.mumu12641.ui.svg

import androidx.compose.ui.graphics.vector.ImageVector
import io.github.mumu12641.ui.svg.svgimage.Welcome
import kotlin.collections.List as ____KtList

public object SvgImage

private var __AllIcons: ____KtList<ImageVector>? = null

public val SvgImage.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= listOf(Welcome)
    return __AllIcons!!
  }
