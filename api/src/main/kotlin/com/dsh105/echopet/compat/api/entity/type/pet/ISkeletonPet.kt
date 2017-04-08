package com.dsh105.echopet.compat.api.entity.type.pet

import com.dsh105.echopet.compat.api.entity.IPet
import com.dsh105.echopet.compat.api.entity.SkeletonType

interface ISkeletonPet: IPet {
    var skeletonType: SkeletonType
}