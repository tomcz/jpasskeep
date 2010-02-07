/*
 * Copyright (c) 2005-2010, Thomas Czarniecki
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  * Neither the name of JPasskeep, Thomas Czarniecki, tomczarniecki.com nor
 *    the names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.tomczarniecki.jpasskeep;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Adapted from http://developer.apple.com/mac/library/samplecode/OSXAdapter/listing3.html
 */
public class OsxQuitAdaptor implements InvocationHandler {

    private final QuitHandler handler;

    public OsxQuitAdaptor(QuitHandler handler) {
        this.handler = handler;
    }

    public static void setQuitHandler(QuitHandler handler) {
        try {
            Class applicationClass = Class.forName("com.apple.eawt.Application");
            Method method = applicationClass.getMethod("getApplication");
            Object application = method.invoke(null);

            Class listenerClass = Class.forName("com.apple.eawt.ApplicationListener");
            Method addListenerMethod = applicationClass.getDeclaredMethod("addApplicationListener", listenerClass);

            Class[] interfaces = {listenerClass};
            InvocationHandler adaptor = new OsxQuitAdaptor(handler);
            Object listener = Proxy.newProxyInstance(OsxQuitAdaptor.class.getClassLoader(), interfaces, adaptor);

            addListenerMethod.invoke(application, listener);

        } catch (Exception e) {
            System.err.println("Apple EAWT event handling is disabled: " + e);
        }
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("handleQuit") && (args.length == 1)) {
            setEventHandled(args[0], handler.quit());
        }
        return null;
    }

    private void setEventHandled(Object event, boolean handled) {
        try {
            Method setHandledMethod = event.getClass().getDeclaredMethod("setHandled", boolean.class);
            setHandledMethod.invoke(event, handled);

        } catch (Exception e) {
            System.err.println("Unable to handle event: " + event);
            e.printStackTrace();
        }

    }
}
