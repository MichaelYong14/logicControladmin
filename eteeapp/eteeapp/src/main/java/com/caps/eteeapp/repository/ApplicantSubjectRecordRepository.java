package com.caps.eteeapp.repository;

import com.caps.eteeapp.model.ApplicantSubjectRecord;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicantSubjectRecordRepository extends JpaRepository<ApplicantSubjectRecord, Long> {

    @EntityGraph(attributePaths = {"subject", "subject.semester", "subject.semester.curriculum", "subject.semester.curriculum.department", "applicant"})
    List<ApplicantSubjectRecord> findByApplicant_ApplicantId(Long applicantId);

    @EntityGraph(attributePaths = {"subject", "subject.semester", "subject.semester.curriculum", "subject.semester.curriculum.department", "applicant"})
    Optional<ApplicantSubjectRecord> findById(Long id);

    List<ApplicantSubjectRecord> findBySubject_Id(Long subjectId);

    List<ApplicantSubjectRecord> findByApplicant_ApplicantIdAndSubject_Semester_YearLevel(Long applicantId, Integer yearLevel);

    List<ApplicantSubjectRecord> findByStatus(ApplicantSubjectRecord.RecordStatus status);

    List<ApplicantSubjectRecord> findByApplicant_ApplicantIdAndStatus(Long applicantId, ApplicantSubjectRecord.RecordStatus status);
}
