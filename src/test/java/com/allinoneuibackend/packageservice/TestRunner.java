package com.allinoneuibackend.packageservice;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {
    public static void main(String[] args) {
        Result controllerResult = JUnitCore.runClasses(FileControllerTest.class);
        Result serviceResult = JUnitCore.runClasses(FileStorageServiceTest.class);

        for (Failure failure : controllerResult.getFailures()) {
            System.out.println(failure.toString());
        }

        for (Failure failure : serviceResult.getFailures()) {
            System.out.println(failure.toString());
        }

        System.out.println("--------------------------------------------------------");
        System.out.println("Controller test status :" + controllerResult.wasSuccessful());
        System.out.println("Controller test run run count :" + controllerResult.getRunCount());
        System.out.println("Controller failures :" + controllerResult.getFailures());
        System.out.println("Controller test run time :" + controllerResult.getRunTime());
        System.out.println("--------------------------------------------------------");
        System.out.println("Service test status :" + serviceResult.wasSuccessful());
        System.out.println("Service test run run count :" + serviceResult.getRunCount());
        System.out.println("Service failures :" + serviceResult.getFailures());
        System.out.println("Service test run time :" + serviceResult.getRunTime());
        System.out.println("--------------------------------------------------------");
    }
}
