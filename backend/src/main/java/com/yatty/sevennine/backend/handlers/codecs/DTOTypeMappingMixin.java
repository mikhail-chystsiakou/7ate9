package com.yatty.sevennine.backend.handlers.codecs;

import com.fasterxml.jackson.databind.annotation.JsonAppend;

@JsonAppend(attrs = {@JsonAppend.Attr(DTOClassMessageTypeMapper.MAPPING_FIELD)})
public abstract class DTOTypeMappingMixin {
}