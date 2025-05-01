from pydantic import BaseModel
from typing import Optional

class OptionBase(BaseModel):
    text: str
    is_correct: bool = False

class OptionCreate(OptionBase):
    pass

class OptionUpdate(OptionBase):
    pass

class OptionResponse(OptionBase):
    id: int
    question_id: int

    class Config:
        orm_mode = True