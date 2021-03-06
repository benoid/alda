#!/usr/bin/env boot

(set-env! :dependencies '[[org.zeromq/jeromq "0.3.5"]])

(import '[org.zeromq ZMQ ZContext])

(defn print-usage
  []
  (println "Example usage:")
  (println "  $ boot scripts/jeromq-sanity-check-server-zcontext.boot 12345")
  (println)
  (println "  # in another terminal:")
  (println "  $ boot scripts/jeromq-sanity-check-client-zcontext.boot 12345"))

(defn hello-world-test
  [socket]
  (println "Hello world client/server example:")
  (dotimes [n 5]
    (let [req (format "Hello #%s from client" (inc n))]
      (println "Sending request:" req)
      (.send socket req))
    (let [res (.recv socket)]
      (println "Received response" (String. res)))))

(defn run-tests
  [port ctx-desc ctx]
  (println)
  (println (format "Testing with %s..." ctx-desc))
  (println)

  (with-open [socket (doto (.createSocket ctx ZMQ/REQ)
                       (.connect (format "tcp://*:%s" port)))]
    (hello-world-test socket))

  (println)
  (print "Destroying context... ")
  (flush)
  (.destroy ctx)
  (println "done."))

(defn -main
  ([]
   (println "No port specified.")
   (println)
   (print-usage)
   (System/exit 1))
  ([port]
   (println (format "Using port %s for tests." port))
   (println
     (format "(In another terminal, run the server script first on port %s.)"
             port))

   (run-tests port "ZContext" (ZContext. 1))

   (println)
   (println "Done.")))
