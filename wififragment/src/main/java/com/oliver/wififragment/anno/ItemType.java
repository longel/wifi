package com.oliver.wififragment.anno;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.oliver.wififragment.constant.Constants.LAYOUT_TYPE_ACTIVE;
import static com.oliver.wififragment.constant.Constants.LAYOUT_TYPE_HEADER;
import static com.oliver.wififragment.constant.Constants.LAYOUT_TYPE_INACTIVE;

/**
 * author : Oliver
 * date   : 2019/8/26
 * desc   :
 */

@IntDef({LAYOUT_TYPE_ACTIVE, LAYOUT_TYPE_HEADER, LAYOUT_TYPE_INACTIVE})
@Retention(RetentionPolicy.SOURCE)
public @interface ItemType {
}
