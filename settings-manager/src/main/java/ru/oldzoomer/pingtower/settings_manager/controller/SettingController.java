package ru.oldzoomer.pingtower.settings_manager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.oldzoomer.pingtower.settings_manager.dto.Setting;
import ru.oldzoomer.pingtower.settings_manager.service.SettingService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/settings")
public class SettingController {

    private final SettingService settingService;

    public SettingController(SettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping
    public ResponseEntity<List<Setting>> getAllSettings() {
        List<Setting> settings = settingService.getAllSettings();
        return ResponseEntity.ok(settings);
    }

    @GetMapping("/{module}")
    public ResponseEntity<List<Setting>> getSettingsByModule(@PathVariable String module) {
        List<Setting> settings = settingService.getSettingsByModule(module);
        return ResponseEntity.ok(settings);
    }

    @GetMapping("/{module}/{key}")
    public ResponseEntity<Setting> getSetting(@PathVariable String module, @PathVariable String key) {
        Optional<Setting> setting = settingService.getSetting(module, key);
        return setting.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Setting> createSetting(@RequestBody Setting setting) {
        Setting createdSetting = settingService.createSetting(setting);
        return ResponseEntity.ok(createdSetting);
    }

    @PutMapping("/{module}/{key}")
    public ResponseEntity<Setting> updateSetting(@PathVariable String module, @PathVariable String key, @RequestBody Setting setting) {
        try {
            Setting updatedSetting = settingService.updateSetting(module, key, setting);
            return ResponseEntity.ok(updatedSetting);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{module}/{key}")
    public ResponseEntity<Void> deleteSetting(@PathVariable String module, @PathVariable String key) {
        try {
            settingService.deleteSetting(module, key);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}