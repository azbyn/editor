(ns editor.test
  (:require
            [editor.utils :refer [case-with-eval case-enum]]
            [editor.graphics-utils :refer :all])
  
  (:import [java.awt Graphics FontMetrics Canvas]
           [java.awt.image BufferStrategy])
  
  (:use clojure.pprint)
  (:use seesaw.color)
  (:use seesaw.dev)
  (:use seesaw.font)
  (:use seesaw.core))
  

(defn hello []
  (println "hello"))
