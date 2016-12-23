#!/usr/bin/env stack
{- stack --install-ghc
    runghc
    --package amqp
    --package bytestring
    --package uuid
-}

-- Anti-virus client

{-# LANGUAGE OverloadedStrings #-}

import Network.AMQP
import Data.UUID
import Data.UUID.V4

--import qualified Data.ByteString.Lazy.Char8 as BL


greeting :: String
greeting = "Example client"

brokerHost :: String
brokerHost = "localhost"

exchange = "check"

testMessage :: UUID -> Message
testMessage uuid = newMsg {
        msgID = Just $ toText uuid,
        msgType = Just "REQUEST",
        msgBody = "TEST MESSAGE"
    }

main :: IO ()
main = do
    putStrLn greeting
    putStrLn $ take (length greeting) (repeat '-')

    connection <- openConnection brokerHost "antivirus" "guest" "guest"
    channel <- openChannel connection

    uuid <- nextRandom

    putStrLn "Sending message:"
    putStrLn $ show $ testMessage uuid

    publishMsg channel exchange "" (testMessage uuid)

    putStrLn "Data sent"
    closeConnection connection
    putStrLn "Connection closed"
