(ns editor.utils
  "Utilities that have nothing to do with java awt"
  (:require [clojure.test :refer :all]))


(defmacro case-enum
  "Like `case`, but explicitly dispatch on Java enum ordinals."
  [e & clauses]
  (letfn [(enum-ordinal [e] `(let [^Enum e# ~e] (.ordinal e#)))]
    `(case ~(enum-ordinal e)
       ~@(concat
          (mapcat (fn [[test result]]
                    [(eval (enum-ordinal test)) result])
                  (partition 2 clauses))
          (when (odd? (count clauses))
            (list (last clauses)))))))

(defmacro case-with-eval
  "Like `case`, but test-constants *are* evaluated.
  Now we can do
  (case-with-eval 2
    (+ 1 1) \"yay\"
    \"nay\")
  "
  [e & clauses]

  `(case ~e
       ~@(concat
          (mapcat (fn [[test result]]
                    [(eval test) result])
                  (partition 2 clauses))
          (when (odd? (count clauses))
            (list (last clauses))))))

(defn number-of-digits
  "Returns the number of digits in `n`.
  Only really works for positive `n`."
  [n]
  (count (str n)))

(defmacro is= [a b]
  `(is (= ~a ~b)))

(with-test #'number-of-digits
  (is= (number-of-digits 42) 2)
  (is= (number-of-digits 0) 1)
  (is= (number-of-digits 1) 1)
  ;; (is= (number-of-digits 1) 2)
  (is= (number-of-digits 10) 2)
  (is= (number-of-digits 123) 3)
  )

(defn clamp
  "Clamps the value of `x` between `low` and `high`.

  The returned value is between `low` and `high`.
  
  Returns `low` if `x` is less than `low`.
  Returns `high` if `x` is greater than `high`.
  Returns `x` unmodified otherwise."
  [x low high]
  (max (min high x) low))


(with-test #'clamp
  (is= (clamp 1 2 3) 2)
  (is= (clamp 2 2 3) 2)
  (is= (clamp 3 2 4) 3)
  (is= (clamp 4 2 4) 4)
  (is= (clamp 6 2 4) 4)

  ;; (is= (clamp 6 2 4) 8)
  )

(defn str-insert
  "Insert c in string s at index i."
  [s c i]
  (str (subs s 0 i) c (subs s i)))

(with-test #'str-insert
  (is= (str-insert "" \a 0 ) "a")
  (is= (str-insert "bc" \a 0) "abc")
  (is= (str-insert "bc" \a 2) "bca")
  (is= (str-insert "bc" \a 1) "bac")
  ;; (is= (clamp 6 2 4) 8)
  )

(defn str-remove
  "Remove the `i`-th character in `s`."
  [s i]
  (str (subs s 0 i) (subs s (inc i))))

(with-test #'str-remove
  (is= (str-remove "abc" 0) "bc")
  (is= (str-remove "abc" 1) "ac")
  (is= (str-remove "abc" 2) "ab")
  (is= (str-remove "a" 0) "")
  )

(defmacro t->
  "Threads the expr through the forms. Inserts x as the second item
   in the first form, making a list of it if it is a lambda or not a
   list already. If there are more forms, inserts the first form as the
   second item in second form, etc.

  `->` that works with lambdas"
  [x & forms]
  (loop [x x, forms forms]
    (if forms
      (let [form (first forms)
            threaded (if (and (seq? form) (not (#{'fn 'fn*} (first form))))
                       (with-meta `(~(first form) ~x ~@(next form)) (meta form))
                       (list form x))]
        (recur threaded (next forms)))
      x)))
