package kieker.tests.loadTimeWeaving.bookstoreWithoutAnnotation;

import java.util.Vector;

/**
 * kieker.tests.loadTimeWeaving.bookstoreWithoutAnnotation.BookstoreWA.java
 *
 * ==================LICENCE=========================
 * Copyright 2006-2008 Matthias Rohr and the Kieker Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================================
 *
 *
 * A simple test and demonstration scenario for Kieker's 
 * monitoring component tpmon. See the kieker tutorial 
 * for more information 
 * (http://www.matthias-rohr.com/kieker/tutorial.html)
 *
 * This test does not use the Kieker typical Java Annotations. The test is used
 * to demonstrate how to instrument programs for that no sources are available.
 *
 * @author Matthias Rohr
 * History:
 * 2008-07-31: Created this file based on Boostore.java
 *
 */

public class BookstoreWA extends Thread{
    static int numberOfRequests = 100;
    static int interRequestTime = 5;

    public static final Vector<BookstoreWA> bookstoreScenarios = new Vector<BookstoreWA>();

    /**
     *
     * main is the load driver for the Bookstore. It creates 
     * request which all request a search from the bookstore. 
     * A fixed time delay is between two request. Requests 
     * are likely to overlap, which leads to request processing
     * in more than one thread. 
     *
     * Both the number of requests and arrival rate are defined 
     * by the local variables above the method.
     * (default: 100 requests; interRequestTime 5 (millisecs))
     * 
     */
    public static void main(String[] args) throws InterruptedException {
	for (int i = 0; i < numberOfRequests; i++) {
    		System.out.println("BookstoreWA.main: Starting request "+i);
		BookstoreWA newBookstore = new BookstoreWA();
		bookstoreScenarios.add(newBookstore);
		newBookstore.start();
		BookstoreWA.waitabit(interRequestTime);
	}
       System.out.println("Bookstore.main: Finished with starting all requests.");
        System.out.println("Bookstore.main: Waiting for threads to terminate");
        synchronized (bookstoreScenarios) {
            while (!bookstoreScenarios.isEmpty()) {
                bookstoreScenarios.wait();
            }
        }
    }

    public void run() {
    	BookstoreWA.searchBook();
        synchronized (bookstoreScenarios) {
            bookstoreScenarios.remove(this);
            bookstoreScenarios.notify();
        }
    }

    public static void searchBook() {
	CatalogWA.getBook(false);	
	CRMWA.getOffers();
    }
   
    /**
     * Only encapsulates Thread.sleep()
     */
    public static void waitabit(long waittime) {
    	if (waittime > 0) {
		try{
		Thread.sleep(waittime);
		} catch(Exception e) {}
	}
    }
} 
