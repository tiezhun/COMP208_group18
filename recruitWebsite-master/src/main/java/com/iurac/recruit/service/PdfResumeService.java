package com.iurac.recruit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iurac.recruit.entity.PdfResume;

import java.util.List;

public interface PdfResumeService extends IService<PdfResume> {
    void saveResume(PdfResume resume);

    PdfResume getLatestResumeByUserId(String userId);
}