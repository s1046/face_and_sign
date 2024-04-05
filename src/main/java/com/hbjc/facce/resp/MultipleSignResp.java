package com.hbjc.facce.resp;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class MultipleSignResp {

    private Long fileBaseLen;

    private String fileName;;

    private String signedPdfBase;

}
