package com.nashirul.zakat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ZakatPenghasilanDto {
    private Long penghasilan;
    private Long penghasilanTambahan;
    private Long pengeluaranPokok;
    private String waktu; // perbulan atau pertahun
}
