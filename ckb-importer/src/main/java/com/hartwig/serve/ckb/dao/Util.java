package com.hartwig.serve.ckb.dao;

final class Util {

    private Util() {
    }

    public static byte toByte(boolean value) {
        return value ? (byte) 1 : (byte) 0;
    }
}
