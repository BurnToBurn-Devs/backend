package com.burntoburn.easyshift.service.templates.imp;

import com.burntoburn.easyshift.dto.template.ScheduleTemplateResponse;
import com.burntoburn.easyshift.dto.template.req.ScheduleTemplateRequest;
import com.burntoburn.easyshift.dto.template.ScheduleTemplateWithShiftsResponse;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import com.burntoburn.easyshift.exception.template.TemplateException;
import com.burntoburn.easyshift.repository.schedule.ScheduleTemplateRepository;
import com.burntoburn.easyshift.repository.store.StoreRepository;
import com.burntoburn.easyshift.service.templates.ScheduleTemplateFactory;
import com.burntoburn.easyshift.service.templates.ScheduleTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ScheduleTemplateServiceImpl implements ScheduleTemplateService {
    private final ScheduleTemplateRepository scheduleTemplateRepository;
    private final StoreRepository storeRepository;
    private final ScheduleTemplateFactory scheduleTemplateFactory;

    @Override
    @Transactional
    public ScheduleTemplateResponse createScheduleTemplate(Long storeId, ScheduleTemplateRequest request) {
        // Store 조회
        Store store = storeRepository.findById(storeId)
                .orElseThrow(()->new NoSuchElementException("not found store"));

        // ScheduleTemplate 생성
        ScheduleTemplate scheduleTemplate = scheduleTemplateFactory.createScheduleTemplate(store, request);

        // ScheduleTemplate 저장
        scheduleTemplateRepository.save(scheduleTemplate);

        return ScheduleTemplateResponse.fromEntity(scheduleTemplate);
    }

    @Transactional(readOnly = true)
    @Override
    public ScheduleTemplateWithShiftsResponse getAllScheduleTemplatesByStore(Long storeId) {
        boolean exists = storeRepository.existsById(storeId);
        if (!exists){
            throw new NoSuchElementException("not found store");
        }

        List<ScheduleTemplate> scheduleTemplateList = scheduleTemplateRepository.findAllByStoreId(storeId);

        // 찾는 스케줄이 비어 있을 때 예외 발생
        if(scheduleTemplateList.isEmpty()){
            throw TemplateException.scheduleTemplateNotFound();
        }

        return ScheduleTemplateWithShiftsResponse.fromEntities(scheduleTemplateList);
    }

    @Override
    public void deleteScheduleTemplate(Long scheduleTemplateId) {
        boolean exists = scheduleTemplateRepository.existsById(scheduleTemplateId);
        if (!exists){
            throw TemplateException.scheduleTemplateNotFound();
        }
        scheduleTemplateRepository.deleteById(scheduleTemplateId);
    }
}
