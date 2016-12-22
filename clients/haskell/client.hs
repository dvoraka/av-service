-- Anti-virus client

{-# LANGUAGE OverloadedStrings #-}

import Network.AMQP

import qualified Data.ByteString.Lazy.Char8 as BL


greeting :: String
greeting = "Example client"

brokerHost :: String
brokerHost = "localhost"

exchange = "check"

main :: IO ()
main = do
    putStrLn greeting
    putStrLn $ take (length greeting) (repeat '-')

    connection <- openConnection brokerHost "antivirus" "guest" "guest"
    channel <- openChannel connection

    publishMsg channel exchange ""
        newMsg {
            msgBody = (BL.pack "TEST DATA")
        }

    putStrLn "Data sent"
    closeConnection connection
    putStrLn "Connection closed"
