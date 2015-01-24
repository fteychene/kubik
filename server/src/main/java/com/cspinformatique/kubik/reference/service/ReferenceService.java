package com.cspinformatique.kubik.reference.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.cspinformatique.kubik.reference.model.Reference;

public interface ReferenceService {
	Iterable<Reference> findByEan13(String ean13, Sort sort);
	
	Iterable<Reference> findByEan13AndImportedInKubik(String ean13, boolean importedInKubik, Sort sort);
	
	Reference findByEan13AndSupplierEan13(String ean13, String supplierEan13);
	
	Page<Reference> findByImportedInKubik(boolean importedInubik, Pageable pageable);
	
	Reference save(Reference reference);
	
	Iterable<? extends Reference> save(List<? extends Reference> references);
	
	Page<Reference> search(String query, String[] fields, Boolean importedInKubik, Pageable pageable);
}