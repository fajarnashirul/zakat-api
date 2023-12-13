package com.nashirul.zakat.controller;

import com.nashirul.zakat.dto.ZakatMaalDto;
import com.nashirul.zakat.dto.ZakatPenghasilanDto;
import com.nashirul.zakat.dto.ZakatPerdaganganDto;
import com.nashirul.zakat.service.ZakatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ZakatController {

    @Autowired
    private final ZakatService zakatService;

    public ZakatController(ZakatService zakatService) {
        this.zakatService = zakatService;
    }

    @GetMapping(path = "/zakat/penghasilan")
    public ResponseEntity<Map<String, Object>>  countZakatPenghasilan (@RequestBody ZakatPenghasilanDto zakatPenghasilanDto){

        Long result = zakatService.countZakatPenghasilan(zakatPenghasilanDto);

        Map<String, Object> response = new HashMap<>();
        if (result != null) {
            response.put("result", result);
            response.put("message", "Jumlah Zakat");
        } else {
            response.put("result", null);
            response.put("message", "Tidak perlu membayar zakat, namun dapat berinfak");
        }
        return ResponseEntity.ok(response);
    }
    @GetMapping(path = "/zakat/perdagangan")
    public ResponseEntity<Map<String, Object>> countZakatPerdagangan (@RequestBody ZakatPerdaganganDto zakatPerdaganganDto){

        Long result = zakatService.countZakatPerdagangan(zakatPerdaganganDto);
        Map<String, Object> response = new HashMap<>();
        if (result != null) {
            response.put("result", result);
            response.put("message", "Jumlah Zakat");
        } else {
            response.put("result", null);
            response.put("message", "Tidak perlu membayar zakat, namun dapat berinfak");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/zakat/maal")
    public ResponseEntity<Map<String, Object>> countZakatMaal (@RequestBody ZakatMaalDto zakatMaalDto){

        Long result = zakatService.countZakatMaal(zakatMaalDto);

        Map<String, Object> response = new HashMap<>();
        if (result != null) {
            response.put("result", result);
            response.put("message", "Jumlah Zakat");
        } else {
            response.put("result", null);
            response.put("message", "Tidak perlu membayar zakat, namun dapat berinfak");
        }
        return ResponseEntity.ok(response);
    }
}
