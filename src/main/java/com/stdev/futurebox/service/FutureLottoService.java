package com.stdev.futurebox.service;

import com.stdev.futurebox.domain.FutureLotto;
import com.stdev.futurebox.repository.FutureLottoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class FutureLottoService {

    private final FutureLottoRepository futureLottoRepository;

    public Long create(FutureLotto futureLotto) {
        try {
            futureLottoRepository.save(futureLotto);
            return futureLotto.getId();
        } catch (Exception e) {
            throw new IllegalArgumentException("Creating FutureLotto failed.");
        }
    }

    @Transactional(readOnly = true)
    public FutureLotto findById(Long id) {
        try {
            return futureLottoRepository.findById(id);
        } catch (Exception e) {
            throw new IllegalArgumentException("FutureLotto not found.");
        }
    }

    @Transactional(readOnly = true)
    public FutureLotto findByBoxId(Long boxId) {
        try {
            return futureLottoRepository.findByBoxId(boxId);
        } catch (Exception e) {
            throw new IllegalArgumentException("FutureLotto not found.");
        }
    }

    public void update(FutureLotto futureLotto) {
        try {
            futureLottoRepository.update(futureLotto);
        } catch (Exception e) {
            throw new IllegalArgumentException("Updating FutureLotto failed.");
        }
    }

    public void delete(Long id) {
        try {
            futureLottoRepository.deleteById(id);
        } catch (Exception e) {
            throw new IllegalArgumentException("Deleting FutureLotto failed.");
        }
    }

    public void deleteByBoxId(Long boxId) {
        try {
            futureLottoRepository.deleteByBoxId(boxId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Deleting FutureLotto failed.");
        }
    }
}