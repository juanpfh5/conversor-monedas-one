package com.one.conversormonedas.modelos;

import java.util.Map;

public record ValoresMoneda(String result, String base_code, Map<String, Double> conversion_rates) {}
