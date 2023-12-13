package com.nashirul.zakat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ZakatPerdaganganDto {
    private Long modal;
    private Long HutangJangkaPendek; // jatuh tempo dalam 1 tahun
}
