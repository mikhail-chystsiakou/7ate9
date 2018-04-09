package com.yatty.sevennine.util.codecs;

import com.fasterxml.jackson.databind.annotation.JsonAppend;

@JsonAppend(attrs = {@JsonAppend.Attr(DTOClassMessageTypeMapper.MAPPING_FIELD)})
abstract class DTOTypeMappingMixin {
}