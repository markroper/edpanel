package com.scholarscore.api.persistence.mysql;

import java.util.Collection;

import com.scholarscore.models.Term;

public interface TermPersistence {
    
    public Collection<Term> selectAllTerms(
            long schoolYearId);

    public Term selectTerm(
            long schoolYearId,
            long termId);

    public Long insertTerm(
            long schoolYearId,
            Term term);

    public Long updateTerm(
            long schoolYearId,
            long termId,
            Term term);

    public Long deleteTerm(long termId);
}
