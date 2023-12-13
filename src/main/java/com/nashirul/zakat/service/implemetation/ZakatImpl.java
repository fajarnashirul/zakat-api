package com.nashirul.zakat.service.implemetation;

import com.nashirul.zakat.dto.ZakatMaalDto;
import com.nashirul.zakat.dto.ZakatPenghasilanDto;
import com.nashirul.zakat.dto.ZakatPerdaganganDto;
import com.nashirul.zakat.service.ZakatService;
import org.springframework.stereotype.Service;

@Service
public class ZakatImpl implements ZakatService {

    private final Long goldPrice = 1100000L; // price per gram based on www.harga-emas.org 13/12/2023
    private final Long nishab = goldPrice * 86;

    @Override
    public  Long countZakatPenghasilan (ZakatPenghasilanDto zakatPenghasilanDto) {
        Long pernghasilanBruto = zakatPenghasilanDto.getPenghasilan() + zakatPenghasilanDto.getPenghasilanTambahan() - zakatPenghasilanDto.getPengeluaranPokok();

        if (zakatPenghasilanDto.getWaktu() == "PERTAHUN" && pernghasilanBruto >= nishab){
            Long result = pernghasilanBruto * 25 /1000;
            return result;
        }else if ((zakatPenghasilanDto.getWaktu() == "PERBULAN" && pernghasilanBruto >= nishab/12)){
            Long result = pernghasilanBruto * 25 /1000;
            return result;
        }
        return null;
    }

    @Override
    public Long countZakatPerdagangan(ZakatPerdaganganDto zakatPerdaganganDto) {

        Long modalBersih = zakatPerdaganganDto.getModal() - zakatPerdaganganDto.getHutangJangkaPendek();
        if ( modalBersih >= nishab){
            Long result = modalBersih * 25 /1000;
            return result;
        }
        return null;
    }

    @Override
    public Long countZakatMaal(ZakatMaalDto zakatMaalDto) {
        if ( zakatMaalDto.getNominal() >= nishab) {
            Long result = zakatMaalDto.getNominal() * 25 / 1000;
            return result;
        }
        return null;
    }
}
