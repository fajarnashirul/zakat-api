package com.nashirul.zakat.service;

import com.nashirul.zakat.dto.ZakatMaalDto;
import com.nashirul.zakat.dto.ZakatPenghasilanDto;
import com.nashirul.zakat.dto.ZakatPerdaganganDto;


public interface ZakatService {
    Long countZakatPenghasilan (ZakatPenghasilanDto zakatPenghasilanDto);
    Long countZakatPerdagangan (ZakatPerdaganganDto zakatPerdaganganDto);
    Long countZakatMaal (ZakatMaalDto zakatMaalDto);
}

